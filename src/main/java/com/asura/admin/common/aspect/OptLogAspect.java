package com.asura.admin.common.aspect;

import com.asura.admin.common.annotation.OptLog;
import com.asura.admin.common.enums.BusinessStatus;
import com.asura.admin.entity.SysOperationLog;
import com.asura.admin.security.LoginUser;
import com.asura.admin.security.TokenService;
import com.asura.admin.service.SysOperationLogService;
import com.asura.admin.util.*;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @Author Rhett
 * @Date 2021/8/18
 * @Description 操作日志记录处理
 */
@Aspect
@Component
public class OptLogAspect {
    private static final Logger log = LoggerFactory.getLogger(OptLogAspect.class);

    @Autowired
    private SysOperationLogService operationLogService;

    // 配置织入点
    @Pointcut("@annotation(com.asura.admin.common.annotation.OptLog)")
    public void logPointCut() {
    }

    /**
     * 处理完请求后执行
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "logPointCut()", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Object jsonResult) {
        handleLog(joinPoint, null, jsonResult);
    }

    /**
     * 拦截异常操作
     * @param joinPoint 切点
     * @param e 异常
     */
    @AfterThrowing(value = "logPointCut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        handleLog(joinPoint, e, null);
    }

    protected void handleLog(final JoinPoint joinPoint, final Exception e, Object jsonResult) {
        try {
            // 获得注解
            OptLog controllerLog = getAnnotationLog(joinPoint);
            if (controllerLog == null) {
                return;
            }
            // 获取当前的用户
            LoginUser loginUser = SpringUtils.getBean(TokenService.class).getLoginUser(ServletUtils.getRequest());

            // *========数据库日志=========*//
            SysOperationLog operationLog = new SysOperationLog();
            //操作时间
            operationLog.setOptTime(new Date());
            operationLog.setStatus(BusinessStatus.SUCCESS.ordinal());
            // 请求的地址
            String ip = ServletUtils.getClientIP();
            // 设置IP地址
            operationLog.setOptIp(ip);
            // 设置操作地点
            operationLog.setOptLocation(IPUtils.getRealAddressByIP(ip));
            // 返回参数
            operationLog.setJsonResult(JacksonUtils.toJsonString(jsonResult));
            // 请求URL
            operationLog.setOptUrl(Objects.requireNonNull(ServletUtils.getRequest()).getRequestURI());
            if (loginUser != null) {
                // 设置操作人员
                operationLog.setOptName(loginUser.getUsername());
            }
            if (e != null) {
                operationLog.setStatus(BusinessStatus.FAIL.ordinal());
                // 设置错误消息
                operationLog.setErrorMsg(StringUtil.substring(e.getMessage(), 0, 2000));
            }
            // 设置方法名称
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            // 设置请求方法
            operationLog.setMethod(className + "." + methodName + "()");
            // 设置请求方式
            operationLog.setRequestMethod(ServletUtils.getRequest().getMethod());
            // 处理设置注解上的参数
            getControllerMethodDescription(joinPoint, controllerLog, operationLog);
            // 保存数据库
            operationLogService.insertOperationLog(operationLog);

        } catch (Exception exp) {
            // 记录本地异常日志
            log.error("==前置通知异常==");
            log.error("异常信息:{}", exp.getMessage());
            exp.printStackTrace();
        }
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     * @param optLog 日志
     * @param operationLog 操作日志
     */
    public void getControllerMethodDescription(JoinPoint joinPoint, OptLog optLog, SysOperationLog operationLog) {
        // 设置action动作
        operationLog.setBusinessType(optLog.businessType().ordinal());
        // 设置标题
        operationLog.setTitle(optLog.title());
        // 设置操作人类别
        operationLog.setOperatorType(optLog.operatorType().ordinal());
        // 是否需要保存request，参数和值
        if (optLog.isSaveRequestData()) {
            // 获取参数的信息，传入到数据库中。
            setRequestValue(joinPoint, operationLog);
        }
    }

    /**
     * 获取请求的参数，放到log中
     * @param operationLog 操作日志
     */
    private void setRequestValue(JoinPoint joinPoint, SysOperationLog operationLog) {
        String requestMethod = operationLog.getRequestMethod();
        if (HttpMethod.PUT.name().equals(requestMethod) || HttpMethod.POST.name().equals(requestMethod)) {
            String params = argsArrayToString(joinPoint.getArgs());
            operationLog.setOptParam(StringUtil.substring(params, 0, 2000));
        } else {
            Map<?, ?> paramsMap = (Map<?, ?>) Objects.requireNonNull(ServletUtils.getRequest()).getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            operationLog.setOptParam(StringUtil.substring(paramsMap.toString(), 0, 2000));
        }
    }

    /**
     * 是否存在注解，如果存在就获取
     */
    private OptLog getAnnotationLog(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();

        if (method != null) {
            return method.getAnnotation(OptLog.class);
        }
        return null;
    }

    /**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray) {
        StringBuilder params = new StringBuilder();
        if (paramsArray != null && paramsArray.length > 0) {
            for (Object o : paramsArray) {
                if (StringUtil.isNotNull(o) && !isFilterObject(o)) {
                    params.append(JacksonUtils.toJsonString(o)).append(" ");
                }
            }
        }
        return params.toString().trim();
    }

    /**
     * 判断是否需要过滤的对象。
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(final Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection collection = (Collection) o;
            for (Object value : collection) {
                return value instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map) o;
            for (Object value : map.entrySet()) {
                Map.Entry entry = (Map.Entry) value;
                return entry.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
                || o instanceof BindingResult;
    }
}

package com.asura.admin.common.interceptor;

import com.asura.admin.common.annotation.RepeatSubmit;
import com.asura.admin.common.constant.RedisConstants;
import com.asura.admin.common.constant.SecurityConstants;
import com.asura.admin.common.filter.RepeatedlyRequestWrapper;
import com.asura.admin.config.properties.RepeatSubmitProperties;
import com.asura.admin.util.JacksonUtils;
import com.asura.admin.util.RedisCache;
import com.asura.admin.util.StringUtil;
import com.asura.admin.util.http.HttpHelper;
import com.asura.admin.util.text.Convert;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author Rhett
 * @Date 2021/8/9
 * @Description
 * 判断请求url和数据是否和上一次相同，
 * <p>
 * 如果和上次相同，则是重复提交表单。 有效时间为10秒内
 */
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SameUrlDataInterceptor extends RepeatSubmitInterceptor {
    public final String REPEAT_PARAMS = "repeatParams";

    public final String REPEAT_TIME = "repeatTime";

    private final RepeatSubmitProperties repeatSubmitProperties;
    private final RedisCache redisCache;

    /**
     * 间隔时间，单位:秒 默认10秒
     * 两次相同参数的请求，如果间隔时间大于该参数，系统不会认定为重复提交的数据
     */
    //private int intervalTime = 10;

    @SuppressWarnings("unchecked")
    @Override
    public boolean isRepeatSubmit(RepeatSubmit repeatSubmit, HttpServletRequest request) {
        // 如果注解不为0 则使用注解数值
        long intervalTime = repeatSubmitProperties.getIntervalTime();
        if (repeatSubmit.intervalTime() > 0) {
            intervalTime = repeatSubmit.timeUnit().toMillis(repeatSubmit.intervalTime());
        }
        String nowParams = "";
        if (request instanceof RepeatedlyRequestWrapper) {
            RepeatedlyRequestWrapper repeatedlyRequest = (RepeatedlyRequestWrapper) request;
            nowParams = HttpHelper.getBodyString(repeatedlyRequest);
        }
        // body参数为空，获取Parameter的数据
        if (StringUtil.isEmpty(nowParams)) {
            nowParams = JacksonUtils.toJsonString(request.getParameterMap());
        }
        Map<String, Object> nowDataMap = new HashMap<>();
        nowDataMap.put(REPEAT_PARAMS, nowParams);
        nowDataMap.put(REPEAT_TIME, System.currentTimeMillis());
        // 请求地址（作为存放cache的key值）
        String url = request.getRequestURI();
        // 唯一值（没有消息头则使用请求地址）
        String submitKey = request.getHeader(SecurityConstants.TOKEN_HEADER);
        if (StringUtil.isEmpty(submitKey)) {
            submitKey = url;
        }
        // 唯一标识（指定key + 消息头）
        String cacheRepeatKey = RedisConstants.REPEAT_SUBMIT_KEY + submitKey;
        Object sessionObj = redisCache.getCacheObject(cacheRepeatKey);
        if (sessionObj != null) {
            Map<String, Object> sessionMap = (Map<String, Object>) sessionObj;
            if (sessionMap.containsKey(url)) {
                Map<String, Object> preDataMap = (Map<String, Object>) sessionMap.get(url);
                if (compareParams(nowDataMap, preDataMap) && compareTime(nowDataMap, preDataMap, intervalTime)) {
                    return true;
                }
            }
        }
        Map<String, Object> cacheMap = new HashMap<>();
        cacheMap.put(url, nowDataMap);
        redisCache.setCacheObject(cacheRepeatKey, cacheMap, Convert.toInt(intervalTime), TimeUnit.MILLISECONDS);
        return false;
    }

    /**
     * 判断参数是否相同
     */
    private boolean compareParams(Map<String, Object> nowMap, Map<String, Object> preMap) {
        String nowParams = (String) nowMap.get(REPEAT_PARAMS);
        String preParams = (String) preMap.get(REPEAT_PARAMS);
        return nowParams.equals(preParams);
    }

    /**
     * 判断两次间隔时间
     */
    private boolean compareTime(Map<String, Object> nowMap, Map<String, Object> preMap, long intervalTime) {
        long time1 = (Long) nowMap.get(REPEAT_TIME);
        long time2 = (Long) preMap.get(REPEAT_TIME);
        return (time1 - time2) < intervalTime;
    }
}

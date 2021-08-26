package com.asura.admin.service.impl;

import com.asura.admin.common.core.page.TableDataInfo;
import com.asura.admin.entity.SysOperationLog;
import com.asura.admin.mapper.SysOperationLogMapper;
import com.asura.admin.service.SysOperationLogService;
import com.asura.admin.util.PageUtils;
import com.asura.admin.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 操作日志记录 服务实现类
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Service
public class SysOperationLogServiceImpl extends ServiceImpl<SysOperationLogMapper, SysOperationLog> implements SysOperationLogService {
    @Autowired
    private SysOperationLogMapper operationLogMapper;

    /**
     * 分页查询操作日志列表
     * @param operationLog 操作日志对象
     * @return 操作日志集合
     */
    @Override
    public TableDataInfo<SysOperationLog> selectPageOperationLogList(SysOperationLog operationLog) {
        //查询参数
        Map<String, Object> params = operationLog.getParams();
        LambdaQueryWrapper<SysOperationLog> wrapper = Wrappers.lambdaQuery(SysOperationLog.class)
                .like(StringUtil.isNotEmpty(operationLog.getTitle()), SysOperationLog::getTitle, operationLog.getTitle())
                .eq(operationLog.getBusinessType() != null && operationLog.getBusinessType() > 0, SysOperationLog::getBusinessType, operationLog.getBusinessType())
                .func(f -> {
                    if (StringUtil.isNotEmpty(operationLog.getBusinessTypes())) {
                        f.in(SysOperationLog::getBusinessTypes, Arrays.asList(operationLog.getBusinessTypes()));
                    }
                })
                .eq(operationLog.getStatus() != null && operationLog.getStatus() > 0, SysOperationLog::getStatus, operationLog.getStatus())
                .like(StringUtil.isNotBlank(operationLog.getOptName()), SysOperationLog::getOptName, operationLog.getOptName())
                .apply(StringUtil.isNotEmpty(params.get("beginTime")), "date_format(opt_time,'%y%m%d') >= date_format({0},'%y%m%d')", params.get("beginTime"))
                .apply(StringUtil.isNotEmpty(params.get("endTime")), "date_format(opt_time,'%y%m%d') <= date_format({0},'%y%m%d')", params.get("endTime"));

        return PageUtils.buildDataInfo(page(PageUtils.buildPage("opt_id", "desc"), wrapper));
    }

    /**
     * 新增操作日志
     * @param operationLog 操作日志对象
     */
    @Override
    public void insertOperationLog(SysOperationLog operationLog) {
        operationLog.setOptTime(new Date());
        operationLogMapper.insert(operationLog);

    }

    @Override
    public List<SysOperationLog> selectOperationLogList(SysOperationLog operationLog) {
        Map<String, Object> params = operationLog.getParams();
        return operationLogMapper.selectList(
                Wrappers.lambdaQuery(SysOperationLog.class)
                        .like(StringUtil.isNotEmpty(operationLog.getTitle()), SysOperationLog::getTitle, operationLog.getTitle())
                        .eq(operationLog.getBusinessType() != null && operationLog.getBusinessType() > 0, SysOperationLog::getBusinessType, operationLog.getBusinessType())
                        .func(f -> {
                            if (StringUtil.isNotEmpty(operationLog.getBusinessTypes())) {
                                f.in(SysOperationLog::getBusinessTypes, Arrays.asList(operationLog.getBusinessTypes()));
                            }
                        })
                        .eq(operationLog.getStatus() != null && operationLog.getStatus() > 0, SysOperationLog::getStatus, operationLog.getStatus())
                        .like(StringUtil.isNotBlank(operationLog.getOptName()), SysOperationLog::getOptName, operationLog.getOptName())
                        .apply(StringUtil.isNotEmpty(params.get("beginTime")), "date_format(opt_time,'%y%m%d') >= date_format({0},'%y%m%d')", params.get("beginTime"))
                        .apply(StringUtil.isNotEmpty(params.get("endTime")), "date_format(opt_time,'%y%m%d') <= date_format({0},'%y%m%d')", params.get("endTime"))
                        .orderByDesc(SysOperationLog::getOptId)
        );
    }

    /**
     * 批量删除系统操作日志
     * @param operationIds 需要删除的操作日志ID
     * @return 结果
     */
    @Override
    public int deleteOperationLogByIds(Long[] operationIds) {
        return operationLogMapper.deleteBatchIds(Arrays.asList(operationIds));
    }

    /**
     * 查询操作日志详细
     * @param operationId 操作ID
     * @return 操作日志对象
     */
    @Override
    public SysOperationLog selectOperationLogById(Long operationId) {
        return operationLogMapper.selectById(operationId);
    }

    /**
     * 清空操作日志
     */
    @Override
    public void cleanOperationLog() {
        remove(Wrappers.lambdaQuery(SysOperationLog.class));
    }
}

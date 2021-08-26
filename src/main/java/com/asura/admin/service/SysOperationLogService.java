package com.asura.admin.service;

import com.asura.admin.common.core.page.TableDataInfo;
import com.asura.admin.entity.SysOperationLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 操作日志记录 服务类
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
public interface SysOperationLogService extends IService<SysOperationLog> {

    /**
     * 分页查询操作日志列表
     * @param operationLog 操作日志对象
     * @return 操作日志集合
     */
    TableDataInfo<SysOperationLog> selectPageOperationLogList(SysOperationLog operationLog);

    /**
     * 新增操作日志
     * @param operationLog 操作日志对象
     */
    void insertOperationLog(SysOperationLog operationLog);

    /**
     * 查询系统操作日志集合
     * @param operationLog 操作日志对象
     * @return 操作日志集合
     */
    List<SysOperationLog> selectOperationLogList(SysOperationLog operationLog);

    /**
     * 批量删除系统操作日志
     * @param operationIds 需要删除的操作日志ID
     * @return 结果
     */
    int deleteOperationLogByIds(Long[] operationIds);

    /**
     * 查询操作日志详细
     * @param operationId 操作ID
     * @return 操作日志对象
     */
    SysOperationLog selectOperationLogById(Long operationId);

    /**
     * 清空操作日志
     */
    void cleanOperationLog();
}

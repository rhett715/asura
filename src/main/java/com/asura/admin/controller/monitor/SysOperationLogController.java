package com.asura.admin.controller.monitor;

import com.asura.admin.common.annotation.OptLog;
import com.asura.admin.common.core.page.TableDataInfo;
import com.asura.admin.common.core.result.JsonResult;
import com.asura.admin.common.enums.BusinessType;
import com.asura.admin.entity.SysOperationLog;
import com.asura.admin.service.SysOperationLogService;
import com.asura.admin.util.ExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 操作日志记录 前端控制器
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Api(value = "操作日志记录控制器")
@RestController
@RequestMapping("/monitor/optLog")
public class SysOperationLogController {
    @Autowired
    private SysOperationLogService operationLogService;

    @ApiOperation(value = "分页显示操作日志")
    @ApiImplicitParam(name = "operationLog", value = "操作日志对象", required = true, dataType = "SysOperationLog", paramType = "body")
    @PreAuthorize("@ss.hasPermi('monitor:operlog:list')")
    @GetMapping("/list")
    public TableDataInfo<SysOperationLog> list(@RequestBody SysOperationLog operationLog) {
        return operationLogService.selectPageOperationLogList(operationLog);
    }

    @ApiOperation(value = "导出操作日志表格")
    @ApiImplicitParam(name = "operationLog", value = "操作日志对象", required = true, dataType = "SysOperationLog", paramType = "body")
    @OptLog(title = "操作日志", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('monitor:operlog:export')")
    @GetMapping("/export")
    public void export(@RequestBody SysOperationLog operationLog, HttpServletResponse response) {
        List<SysOperationLog> list = operationLogService.selectOperationLogList(operationLog);
        ExcelUtil.exportExcel(list, "操作日志", SysOperationLog.class, response);
    }

    @ApiOperation(value = "批量删除操作日志")
    @ApiImplicitParam(name = "operationIds", value = "操作日志id数组", required = true, dataType = "Long[]", paramType = "path")
    @OptLog(title = "操作日志", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('monitor:operlog:remove')")
    @DeleteMapping("/{operationIds}")
    public JsonResult<String> remove(@PathVariable Long[] operationIds) {
        return operationLogService.deleteOperationLogByIds(operationIds) > 0 ? JsonResult.success() : JsonResult.fail("批量删除操作日志失败");
    }

    @ApiOperation(value = "清空操作日志记录")
    @OptLog(title = "操作日志", businessType = BusinessType.CLEAN)
    @PreAuthorize("@ss.hasPermi('monitor:operlog:remove')")
    @DeleteMapping("/clean")
    public JsonResult<Void> clean() {
        operationLogService.cleanOperationLog();
        return JsonResult.success();
    }
}

package com.asura.admin.controller.monitor;

import com.asura.admin.common.annotation.OptLog;
import com.asura.admin.common.core.page.TableDataInfo;
import com.asura.admin.common.core.result.JsonResult;
import com.asura.admin.common.enums.BusinessType;
import com.asura.admin.entity.SysLoginInfo;
import com.asura.admin.service.SysLoginInfoService;
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
 * 系统访问记录 前端控制器
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Api(value = "系统访问记录控制器")
@RestController
@RequestMapping("/monitor/loginInfo")
public class SysLoginInfoController {
    @Autowired
    private SysLoginInfoService loginInfoService;

    @ApiOperation(value = "分页查询系统登录信息")
    @ApiImplicitParam(name = "loginInfo", value = "访问日志对象", required = true, dataType = "SysLoginInfo", paramType = "body")
    @PreAuthorize("@ss.hasPermi('monitor:loginInfo:list')")
    @GetMapping("/list")
    public TableDataInfo<SysLoginInfo> list(@RequestBody SysLoginInfo loginInfo) {
        return loginInfoService.selectPageLoginInfoList(loginInfo);
    }

    @ApiOperation(value = "导出登录日志")
    @ApiImplicitParam(name = "loginInfo", value = "访问日志对象", required = true, dataType = "SysLoginInfo", paramType = "body")
    @OptLog(title = "登录日志", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('monitor:loginInfo:export')")
    @GetMapping("/export")
    public void export(@RequestBody SysLoginInfo loginInfo, HttpServletResponse response) {
        List<SysLoginInfo> loginInfoList = loginInfoService.selectLoginInfoList(loginInfo);
        ExcelUtil.exportExcel(loginInfoList, "登录日志", SysLoginInfo.class, response);
    }

    @ApiOperation(value = "批量删除访问日志")
    @ApiImplicitParam(name = "infoIds", value = "访问日志ID数组", required = true, dataType = "Long[]", paramType = "path")
    @PreAuthorize("@ss.hasPermi('monitor:loginInfo:remove')")
    @OptLog(title = "登录日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{infoIds}")
    public JsonResult<String> remove(@PathVariable Long[] infoIds) {
        return loginInfoService.deleteLoginInfoByIds(infoIds) > 0 ? JsonResult.success() : JsonResult.fail("批量删除访问日志失败");
    }

    @ApiOperation(value = "清空系统登录日志")
    @PreAuthorize("@ss.hasPermi('monitor:loginInfo:remove')")
    @OptLog(title = "登录日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public JsonResult<Void> clean() {
        loginInfoService.cleanLoginInfo();
        return JsonResult.success();
    }
}

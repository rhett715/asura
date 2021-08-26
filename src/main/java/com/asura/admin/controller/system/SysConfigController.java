package com.asura.admin.controller.system;

import com.asura.admin.common.annotation.OptLog;
import com.asura.admin.common.annotation.RepeatSubmit;
import com.asura.admin.common.constant.UserConstants;
import com.asura.admin.common.core.page.TableDataInfo;
import com.asura.admin.common.core.result.JsonResult;
import com.asura.admin.common.enums.BusinessType;
import com.asura.admin.entity.SysConfig;
import com.asura.admin.service.SysConfigService;
import com.asura.admin.util.ExcelUtil;
import com.asura.admin.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 参数配置表 前端控制器
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Api(value = "参数配置控制器")
@RestController
@RequestMapping("/system/config")
public class SysConfigController {
    @Autowired
    private SysConfigService configService;

    @ApiOperation(value = "获取参数配置列表")
    @ApiImplicitParam(name = "config", value = "参数配置信息", required = true, dataType = "SysConfig", paramType = "body")
    @PreAuthorize("@ss.hasPermi('system:config:list')")
    @GetMapping("/list")
    public TableDataInfo<SysConfig> list(@Validated @RequestBody SysConfig config) {
        return configService.selectPageConfigList(config);
    }

    @ApiOperation(value = "导出参数配置信息")
    @ApiImplicitParam(name = "config", value = "参数配置信息", required = true, dataType = "SysConfig", paramType = "body")
    @OptLog(title = "参数管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:config:export')")
    @GetMapping("/export")
    public void export(@Validated @RequestBody SysConfig config, HttpServletResponse response) {
        List<SysConfig> list = configService.selectConfigList(config);
        ExcelUtil.exportExcel(list, "参数数据", SysConfig.class, response);
    }

    @ApiOperation(value = "根据参数编号获取详细信息")
    @ApiImplicitParam(name = "config", value = "参数配置信息", required = true, dataType = "SysConfig", paramType = "body")
    @PreAuthorize("@ss.hasPermi('system:config:query')")
    @GetMapping(value = "/{configId}")
    public JsonResult<SysConfig> getInfo(@PathVariable Long configId) {
        return JsonResult.success(configService.selectConfigById(configId));
    }

    @ApiOperation(value = "根据参数键名查询参数值")
    @ApiImplicitParam(name = "configKey", value = "参数键名", required = true, dataType = "String", paramType = "path")
    @GetMapping(value = "/configKey/{configKey}")
    public JsonResult<String> getConfigKey(@PathVariable String configKey) {
        return JsonResult.success(configService.selectConfigByKey(configKey));
    }

    @ApiOperation(value = "新增参数配置")
    @ApiImplicitParam(name = "config", value = "参数配置信息", required = true, dataType = "SysConfig", paramType = "body")
    @PreAuthorize("@ss.hasPermi('system:config:add')")
    @OptLog(title = "参数管理", businessType = BusinessType.INSERT)
    @PostMapping
    @RepeatSubmit
    public JsonResult<String> add(@Validated @RequestBody SysConfig config) {
        if (UserConstants.NOT_UNIQUE.equals(configService.checkConfigKeyUnique(config))) {
            return JsonResult.fail("新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        config.setCreateBy(SecurityUtils.getUsername());
        return configService.insertConfig(config)  > 0 ? JsonResult.success() : JsonResult.fail("新增参数信息失败");
    }

    @ApiOperation(value = "修改参数配置")
    @ApiImplicitParam(name = "config", value = "参数配置信息", required = true, dataType = "SysConfig", paramType = "body")
    @PreAuthorize("@ss.hasPermi('system:config:edit')")
    @OptLog(title = "参数管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public JsonResult<String> edit(@Validated @RequestBody SysConfig config) {
        if (UserConstants.NOT_UNIQUE.equals(configService.checkConfigKeyUnique(config))) {
            return JsonResult.fail("修改参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        config.setUpdateBy(SecurityUtils.getUsername());
        return configService.updateConfig(config) > 0 ? JsonResult.success() : JsonResult.fail("修改参数配置信息失败");
    }

    @ApiOperation(value = "删除参数配置")
    @ApiImplicitParam(name = "configIds", value = "参数配置ID", required = true, dataType = "Long[]", paramType = "path")
    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @OptLog(title = "参数管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    public JsonResult<Void> remove(@PathVariable Long[] configIds) {
        configService.deleteConfigByIds(configIds);
        return JsonResult.success();
    }

    @ApiOperation(value = "刷新参数缓存")
    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @OptLog(title = "参数管理", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public JsonResult<Void> refreshCache() {
        configService.resetConfigCache();
        return JsonResult.success();
    }
}

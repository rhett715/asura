package com.asura.admin.controller.system;

import com.asura.admin.common.annotation.OptLog;
import com.asura.admin.common.core.page.TableDataInfo;
import com.asura.admin.common.core.result.JsonResult;
import com.asura.admin.common.enums.BusinessType;
import com.asura.admin.entity.SysNotice;
import com.asura.admin.service.SysNoticeService;
import com.asura.admin.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 通知公告表 前端控制器
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Api(value = "公告信息控制器")
@RestController
@RequestMapping("/system/notice")
public class SysNoticeController {
    @Autowired
    private SysNoticeService noticeService;

    @ApiOperation(value = "获取通知公告列表")
    @ApiImplicitParam(name = "notice", value = "通知公告信息", required = true, dataType = "SysNotice", paramType = "body")
    @PreAuthorize("@ss.hasPermi('system:notice:list')")
    @GetMapping("/list")
    public TableDataInfo<SysNotice> list(@RequestBody SysNotice notice) {
        return noticeService.selectPageNoticeList(notice);
    }

    @ApiOperation(value = "根据通知公告编号获取详细信息")
    @ApiImplicitParam(name = "noticeId", value = "公告ID", required = true, dataType = "Long", paramType = "path")
    @PreAuthorize("@ss.hasPermi('system:notice:query')")
    @GetMapping(value = "/{noticeId}")
    public JsonResult<SysNotice> getInfo(@PathVariable Long noticeId) {
        return JsonResult.success(noticeService.selectNoticeById(noticeId));
    }

    @ApiOperation(value = "新增通知公告")
    @ApiImplicitParam(name = "notice", value = "通知公告信息", required = true, dataType = "SysNotice", paramType = "body")
    @PreAuthorize("@ss.hasPermi('system:notice:add')")
    @OptLog(title = "通知公告", businessType = BusinessType.INSERT)
    @PostMapping
    public JsonResult<String> add(@Validated @RequestBody SysNotice notice) {
        notice.setCreateBy(SecurityUtils.getUsername());
        return noticeService.insertNotice(notice) > 0 ? JsonResult.success() : JsonResult.fail("新增通知公告失败");
    }

    @ApiOperation(value = "修改通知公告")
    @ApiImplicitParam(name = "notice", value = "通知公告信息", required = true, dataType = "SysNotice", paramType = "body")
    @PreAuthorize("@ss.hasPermi('system:notice:edit')")
    @OptLog(title = "通知公告", businessType = BusinessType.UPDATE)
    @PutMapping
    public JsonResult<String> edit(@Validated @RequestBody SysNotice notice) {
        notice.setUpdateBy(SecurityUtils.getUsername());
        return noticeService.updateNotice(notice) > 0 ? JsonResult.success() : JsonResult.fail("修改通知公告失败");
    }

    @ApiOperation(value = "删除通知公告")
    @ApiImplicitParam(name = "noticeIds", value = "公告ID数组", required = true, dataType = "Long[]", paramType = "path")
    @PreAuthorize("@ss.hasPermi('system:notice:remove')")
    @OptLog(title = "通知公告", businessType = BusinessType.DELETE)
    @DeleteMapping("/{noticeIds}")
    public JsonResult<String> remove(@PathVariable Long[] noticeIds) {
        return noticeService.deleteNoticeByIds(noticeIds) > 0 ? JsonResult.success() : JsonResult.fail("删除通知公告失败");
    }
}

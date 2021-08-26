package com.asura.admin.controller.system;

import com.asura.admin.common.annotation.OptLog;
import com.asura.admin.common.constant.UserConstants;
import com.asura.admin.common.core.page.TableDataInfo;
import com.asura.admin.common.core.result.JsonResult;
import com.asura.admin.common.enums.BusinessType;
import com.asura.admin.entity.SysPost;
import com.asura.admin.service.SysPostService;
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
 * 岗位信息表 前端控制器
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Api(value = "岗位信息控制器")
@RestController
@RequestMapping("/system/post")
public class SysPostController {
    @Autowired
    private SysPostService postService;

    @ApiOperation(value = "获取岗位列表")
    @ApiImplicitParam(name = "post", value = "岗位信息", required = true, dataType = "SysPost", paramType = "body")
    @PreAuthorize("@ss.hasPermi('system:post:list')")
    @GetMapping("/list")
    public TableDataInfo<SysPost> list(@Validated @RequestBody SysPost post) {
        return postService.selectPagePostList(post);
    }

    @ApiOperation(value = "导出岗位数据")
    @ApiImplicitParam(name = "post", value = "岗位信息", required = true, dataType = "SysPost", paramType = "body")
    @OptLog(title = "岗位管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:post:export')")
    @GetMapping("/export")
    public void export(@Validated @RequestBody SysPost post, HttpServletResponse response) {
        List<SysPost> list = postService.selectPostList(post);
        ExcelUtil.exportExcel(list, "岗位数据", SysPost.class, response);
    }

    @ApiOperation(value = "根据岗位编号获取详细信息")
    @ApiImplicitParam(name = "postId", value = "岗位信息ID", required = true, dataType = "Long", paramType = "path")
    @PreAuthorize("@ss.hasPermi('system:post:query')")
    @GetMapping(value = "/{postId}")
    public JsonResult<SysPost> getInfo(@PathVariable Long postId) {
        return JsonResult.success(postService.selectPostById(postId));
    }

    @ApiOperation(value = "新增岗位")
    @ApiImplicitParam(name = "post", value = "岗位信息", required = true, dataType = "SysPost", paramType = "body")
    @PreAuthorize("@ss.hasPermi('system:post:add')")
    @OptLog(title = "岗位管理", businessType = BusinessType.INSERT)
    @PostMapping
    public JsonResult<String> add(@Validated @RequestBody SysPost post) {
        if (UserConstants.NOT_UNIQUE.equals(postService.checkPostNameUnique(post))) {
            return JsonResult.fail("新增岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(postService.checkPostCodeUnique(post))) {
            return JsonResult.fail("新增岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        post.setCreateBy(SecurityUtils.getUsername());
        return postService.insertPost(post) > 0 ? JsonResult.success() : JsonResult.fail("新增岗位信息失败");
    }

    @ApiOperation(value = "修改岗位")
    @ApiImplicitParam(name = "post", value = "岗位信息", required = true, dataType = "SysPost", paramType = "body")
    @PreAuthorize("@ss.hasPermi('system:post:edit')")
    @OptLog(title = "岗位管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public JsonResult<String> edit(@Validated @RequestBody SysPost post) {
        if (UserConstants.NOT_UNIQUE.equals(postService.checkPostNameUnique(post))) {
            return JsonResult.fail("修改岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(postService.checkPostCodeUnique(post))) {
            return JsonResult.fail("修改岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        post.setUpdateBy(SecurityUtils.getUsername());
        return postService.updatePost(post) > 0 ? JsonResult.success() : JsonResult.fail("修改岗位信息失败");
    }

    @ApiOperation(value = "删除岗位")
    @ApiImplicitParam(name = "postIds", value = "岗位ID数组", required = true, dataType = "Long[]", paramType = "path")
    @PreAuthorize("@ss.hasPermi('system:post:remove')")
    @OptLog(title = "岗位管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{postIds}")
    public JsonResult<String> remove(@PathVariable Long[] postIds) {
        return postService.deletePostByIds(postIds) > 0 ? JsonResult.success() : JsonResult.fail("删除岗位失败");
    }

    @ApiOperation(value = "获取岗位选择框列表")
    @GetMapping("/optionSelect")
    public JsonResult<List<SysPost>> optionSelect() {
        List<SysPost> posts = postService.selectPostAll();
        return JsonResult.success(posts);
    }
}

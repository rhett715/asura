package com.asura.admin.controller.system;

import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ArrayUtil;
import com.asura.admin.common.annotation.OptLog;
import com.asura.admin.common.constant.UserConstants;
import com.asura.admin.common.core.page.TableDataInfo;
import com.asura.admin.common.core.result.JsonResult;
import com.asura.admin.common.enums.BusinessType;
import com.asura.admin.domain.vo.SysUserExportVo;
import com.asura.admin.domain.vo.SysUserImportVo;
import com.asura.admin.entity.SysDept;
import com.asura.admin.entity.SysRole;
import com.asura.admin.entity.SysUser;
import com.asura.admin.security.LoginUser;
import com.asura.admin.security.TokenService;
import com.asura.admin.service.SysPostService;
import com.asura.admin.service.SysRoleService;
import com.asura.admin.service.SysUserService;
import com.asura.admin.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户信息表 前端控制器
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Api(value = "用户信息控制器")
@RestController
@RequestMapping("/system/user")
public class SysUserController {
    @Autowired
    private SysUserService userService;
    @Autowired
    private SysRoleService roleService;
    @Autowired
    private SysPostService postService;
    @Autowired
    private TokenService tokenService;

    @ApiOperation(value = "获取用户列表")
    @ApiImplicitParam(name = "user", value = "用户信息", required = true, dataType = "SysUser", paramType = "body")
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    public TableDataInfo<SysUser> list(@RequestBody SysUser user) {
        return userService.selectPageUserList(user);
    }

    @ApiOperation(value = "导出用户数据")
    @ApiImplicitParam(name = "user", value = "用户信息", required = true, dataType = "SysUser", paramType = "body")
    @OptLog(title = "用户管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:user:export')")
    @GetMapping("/export")
    public void export(@RequestBody SysUser user, HttpServletResponse response) {
        List<SysUser> list = userService.selectUserList(user);
        List<SysUserExportVo> listVo = BeanCopyUtils.listCopy(list, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true), SysUserExportVo.class);
        for (int i = 0; i < list.size(); i++) {
            SysDept dept = list.get(i).getDept();
            SysUserExportVo vo = listVo.get(i);
            if (StringUtil.isNotEmpty(dept)) {
                vo.setDeptName(dept.getDeptName());
                vo.setLeader(dept.getLeader());
            }
        }
        ExcelUtil.exportExcel(listVo, "用户数据", SysUserExportVo.class, response);
    }

    @ApiOperation(value = "从文件中导入用户数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "来自表单的上传文件", required = true, dataType = "MultipartFile", paramType = "query"),
            @ApiImplicitParam(name = "updateSupport", value = "是否更新支持", required = true, dataType = "boolean", paramType = "query")
    })
    @OptLog(title = "用户管理", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('system:user:import')")
    @PostMapping("/importData")
    public JsonResult<String> importData(@RequestParam MultipartFile file, @RequestParam boolean updateSupport) throws Exception {
        List<SysUserImportVo> userListVo = ExcelUtil.importExcel(file.getInputStream(), SysUserImportVo.class);
        List<SysUser> userList = BeanCopyUtils.listCopy(userListVo, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true), SysUser.class);
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        String optName = loginUser.getUsername();
        String message = userService.importUser(userList, updateSupport, optName);
        return JsonResult.success(message);
    }

    @ApiOperation(value = "导入数据模板")
    @GetMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil.exportExcel(new ArrayList<>(), "用户数据", SysUserImportVo.class, response);
    }


    @ApiOperation(value = "根据用户编号获取详细信息")
    @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "Long", paramType = "path")
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping(value = { "/", "/{userId}" })
    public JsonResult<Map<String, Object>> getInfo(@PathVariable(value = "userId", required = false) Long userId) {
        Map<String, Object> result = new HashMap<>();
        List<SysRole> roles = roleService.selectRoleAll();
        result.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        result.put("posts", postService.selectPostAll());
        if (StringUtil.isNotNull(userId)) {
            result.put("user", userService.selectUserById(userId));
            result.put("postIds", postService.selectPostListByUserId(userId));
            result.put("roleIds", roleService.selectRoleListByUserId(userId));
        }
        return JsonResult.success(result);
    }

    @ApiOperation(value = "新增用户")
    @ApiImplicitParam(name = "user", value = "用户信息", required = true, dataType = "SysUser", paramType = "body")
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @OptLog(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public JsonResult<String> add(@Validated @RequestBody SysUser user) {
        if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(user.getUserName()))) {
            return JsonResult.fail("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (StringUtil.isNotEmpty(user.getPhoneNumber())
                && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            return JsonResult.fail("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtil.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return JsonResult.fail("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setCreateBy(SecurityUtils.getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return userService.insertUser(user) > 0 ? JsonResult.success() : JsonResult.fail("新增用户信息失败");
    }

    @ApiOperation(value = "修改用户")
    @ApiImplicitParam(name = "user", value = "用户信息", required = true, dataType = "SysUser", paramType = "body")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @OptLog(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public JsonResult<String> edit(@Validated @RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        if (StringUtil.isNotEmpty(user.getPhoneNumber())
                && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            return JsonResult.fail("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtil.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return JsonResult.fail("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUpdateBy(SecurityUtils.getUsername());
        return userService.updateUser(user) > 0 ? JsonResult.success() : JsonResult.fail("修改用户信息失败");
    }

    @ApiOperation(value = "批量删除用户")
    @ApiImplicitParam(name = "userIds", value = "用户ID数组", required = true, dataType = "Long[]", paramType = "path")
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @OptLog(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public JsonResult<String> remove(@PathVariable Long[] userIds) {
        if (ArrayUtil.contains(userIds, SecurityUtils.getUserId())) {
            return JsonResult.fail("当前用户不能删除");
        }
        return userService.deleteUserByIds(userIds) > 0 ? JsonResult.success() : JsonResult.fail("批量删除用户失败");
    }

    /**
     * 重置密码
     */
    @ApiOperation(value = "重置密码")
    @ApiImplicitParam(name = "user", value = "用户信息", required = true, dataType = "SysUser", paramType = "body")
    @PreAuthorize("@ss.hasPermi('system:user:resetPwd')")
    @OptLog(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public JsonResult<String> resetPwd(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(SecurityUtils.getUsername());
        return userService.resetPwd(user)  > 0 ? JsonResult.success() : JsonResult.fail("重置密码失败");
    }

    @ApiOperation(value = "状态修改")
    @ApiImplicitParam(name = "user", value = "用户信息", required = true, dataType = "SysUser", paramType = "body")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @OptLog(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public JsonResult<String> changeStatus(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        user.setUpdateBy(SecurityUtils.getUsername());
        return userService.updateUserStatus(user) > 0 ? JsonResult.success() : JsonResult.fail("用户状态修改失败");
    }

    @ApiOperation(value = "根据用户编号获取授权角色")
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "Long", paramType = "path")
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping("/authRole/{userId}")
    public JsonResult<Map<String, Object>> authRole(@PathVariable("userId") Long userId) {
        SysUser user = userService.selectUserById(userId);
        List<SysRole> roles = roleService.selectRolesByUserId(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        return JsonResult.success(result);
    }

    @ApiOperation(value = "用户授权角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "roleIds", value = "角色ID数组", required = true, dataType = "Long[]", paramType = "query")
    })
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @OptLog(title = "用户管理", businessType = BusinessType.GRANT)
    @PutMapping("/authRole")
    public JsonResult<Void> insertAuthRole(@RequestParam Long userId, @RequestParam Long[] roleIds) {
        userService.insertUserAuth(userId, roleIds);
        return JsonResult.success();
    }
}

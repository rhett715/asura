package com.asura.admin.controller.system;

import com.asura.admin.common.annotation.OptLog;
import com.asura.admin.common.constant.UserConstants;
import com.asura.admin.common.core.page.TableDataInfo;
import com.asura.admin.common.core.result.JsonResult;
import com.asura.admin.common.enums.BusinessType;
import com.asura.admin.entity.SysRole;
import com.asura.admin.entity.SysUser;
import com.asura.admin.entity.SysUserRole;
import com.asura.admin.security.LoginUser;
import com.asura.admin.security.TokenService;
import com.asura.admin.security.service.SysPermissionService;
import com.asura.admin.service.SysRoleService;
import com.asura.admin.service.SysUserService;
import com.asura.admin.util.ExcelUtil;
import com.asura.admin.util.SecurityUtils;
import com.asura.admin.util.ServletUtils;
import com.asura.admin.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 角色信息表 前端控制器
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Api(value = "角色信息控制器")
@RestController
@RequestMapping("/system/role")
public class SysRoleController {
    @Autowired
    private SysRoleService roleService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private SysPermissionService permissionService;
    @Autowired
    private SysUserService userService;

    @ApiOperation(value = "分页查询角色信息")
    @ApiImplicitParam(name = "role", value = "角色信息", required = true, dataType = "SysRole", paramType = "body")
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/list")
    public TableDataInfo<SysRole> list(@Validated @RequestBody SysRole role) {
        return roleService.selectPageRoleList(role);
    }

    @ApiOperation(value = "导出角色信息数据")
    @ApiImplicitParam(name = "role", value = "角色信息", required = true, dataType = "SysRole", paramType = "body")
    @OptLog(title = "角色管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:role:export')")
    @GetMapping("/export")
    public void export(@Validated @RequestBody SysRole role, HttpServletResponse response) {
        List<SysRole> list = roleService.selectRoleList(role);
        ExcelUtil.exportExcel(list, "角色数据", SysRole.class, response);
    }

    @ApiOperation(value = "根据角色编号获取详细信息")
    @ApiImplicitParam(name = "roleId", value = "用户角色ID", required = true, dataType = "Long", paramType = "path")
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping(value = "/{roleId}")
    public JsonResult<SysRole> getInfo(@PathVariable Long roleId) {
        return JsonResult.success(roleService.selectRoleById(roleId));
    }

    @ApiOperation(value = "新增角色")
    @ApiImplicitParam(name = "role", value = "角色信息", required = true, dataType = "SysRole", paramType = "body")
    @PreAuthorize("@ss.hasPermi('system:role:add')")
    @OptLog(title = "角色管理", businessType = BusinessType.INSERT)
    @PostMapping
    public JsonResult<String> add(@Validated @RequestBody SysRole role) {
        if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role))) {
            return JsonResult.fail("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role))) {
            return JsonResult.fail("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        role.setCreateBy(SecurityUtils.getUsername());
        return roleService.insertRole(role) > 0 ? JsonResult.success() : JsonResult.fail("新增角色信息失败");
    }

    @ApiOperation(value = "修改保存角色")
    @ApiImplicitParam(name = "role", value = "角色信息", required = true, dataType = "SysRole", paramType = "body")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @OptLog(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public JsonResult<String> edit(@Validated @RequestBody SysRole role) {
        roleService.checkRoleAllowed(role);
        if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role))) {
            return JsonResult.fail("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role))) {
            return JsonResult.fail("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        role.setUpdateBy(SecurityUtils.getUsername());

        if (roleService.updateRole(role) > 0) {
            // 更新缓存用户权限
            LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
            if (StringUtil.isNotNull(loginUser.getUser()) && !loginUser.getUser().isAdmin()) {
                loginUser.setPermissions(permissionService.getMenuPermission(loginUser.getUser()));
                loginUser.setUser(userService.selectUserByUserName(loginUser.getUser().getUserName()));
                tokenService.setLoginUser(loginUser);
            }
            return JsonResult.success();
        }
        return JsonResult.fail("修改角色'" + role.getRoleName() + "'失败，请联系管理员");
    }

    @ApiOperation(value = "修改保存数据权限")
    @ApiImplicitParam(name = "role", value = "角色信息", required = true, dataType = "SysRole", paramType = "body")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @OptLog(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/dataScope")
    public JsonResult<String> dataScope(@Validated @RequestBody SysRole role) {
        roleService.checkRoleAllowed(role);
        return roleService.authDataScope(role) > 0 ? JsonResult.success() : JsonResult.fail("修改保存数据权限失败");
    }

    @ApiOperation(value = "角色状态修改")
    @ApiImplicitParam(name = "role", value = "角色信息", required = true, dataType = "SysRole", paramType = "body")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @OptLog(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public JsonResult<String> changeStatus(@Validated @RequestBody SysRole role) {
        roleService.checkRoleAllowed(role);
        role.setUpdateBy(SecurityUtils.getUsername());
        return roleService.updateRoleStatus(role) > 0 ? JsonResult.success() : JsonResult.fail("修改角色状态失败");
    }

    @ApiOperation(value = "批量删除角色")
    @ApiImplicitParam(name = "roleIds", value = "角色信息ID数组", required = true, dataType = "Long[]", paramType = "path")
    @PreAuthorize("@ss.hasPermi('system:role:remove')")
    @OptLog(title = "角色管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roleIds}")
    public JsonResult<String> remove(@PathVariable Long[] roleIds) {
        return roleService.deleteRoleByIds(roleIds)  >0 ? JsonResult.success() : JsonResult.fail("批量删除角色信息失败");
    }

    @ApiOperation(value = "获取角色选择框列表")
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping("/optionSelect")
    public JsonResult<List<SysRole>> optionSelect() {
        return JsonResult.success(roleService.selectRoleAll());
    }

    @ApiOperation(value = "查询已分配用户角色列表")
    @ApiImplicitParam(name = "user", value = "用户信息", required = true, dataType = "SysUser", paramType = "body")
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/authUser/allocatedList")
    public TableDataInfo<SysUser> allocatedList(@Validated @RequestBody SysUser user) {
        return userService.selectAllocatedList(user);
    }

    @ApiOperation(value = "查询未分配用户角色列表")
    @ApiImplicitParam(name = "user", value = "用户信息", required = true, dataType = "SysUser", paramType = "body")
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/authUser/unallocatedList")
    public TableDataInfo<SysUser> unallocatedList(@Validated @RequestBody SysUser user) {
        return userService.selectUnallocatedList(user);
    }

    @ApiOperation(value = "取消授权用户")
    @ApiImplicitParam(name = "userRole", value = "用户角色关联", required = true, dataType = "SysUserRole", paramType = "body")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @OptLog(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancel")
    public JsonResult<String> cancelAuthUser(@RequestBody SysUserRole userRole) {
        return roleService.deleteAuthUser(userRole) > 0 ? JsonResult.success() : JsonResult.fail("取消用户授权失败");
    }

    @ApiOperation(value = "批量取消授权用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "角色ID", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userIds", value = "用户ID数组", required = true, dataType = "Long[]", paramType = "query")
    })
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @OptLog(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancelAll")
    public JsonResult<String> cancelAuthUserAll(@RequestParam Long roleId, @RequestParam Long[] userIds) {
        return roleService.deleteAuthUsers(roleId, userIds) > 0 ? JsonResult.success() : JsonResult.fail("批量取消授权用户出错");
    }

    @ApiOperation(value = "批量选择用户授权")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "角色ID", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userIds", value = "用户ID数组", required = true, dataType = "Long[]", paramType = "query")
    })
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @OptLog(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/selectAll")
    public JsonResult<String> selectAuthUserAll(@RequestParam Long roleId, @RequestParam Long[] userIds) {
        return roleService.insertAuthUsers(roleId, userIds) > 0 ? JsonResult.success() : JsonResult.fail("批量查询用户角色授权出错");
    }
}

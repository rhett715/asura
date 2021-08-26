package com.asura.admin.controller.system;

import com.asura.admin.common.annotation.OptLog;
import com.asura.admin.common.constant.UserConstants;
import com.asura.admin.common.core.result.JsonResult;
import com.asura.admin.common.enums.BusinessType;
import com.asura.admin.domain.TreeSelect;
import com.asura.admin.entity.SysMenu;
import com.asura.admin.service.SysMenuService;
import com.asura.admin.util.SecurityUtils;
import com.asura.admin.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 菜单权限表 前端控制器
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Api(value = "菜单权限信息控制器")
@RestController
@RequestMapping("/system/menu")
public class SysMenuController {
    @Autowired
    private SysMenuService menuService;

    @ApiOperation(value = "获取菜单列表")
    @ApiImplicitParam(name = "menu", value = "菜单信息对象", required = true, dataType = "SysMenu", paramType = "body")
    @PreAuthorize("@ss.hasPermi('system:menu:list')")
    @GetMapping("/list")
    public JsonResult<List<SysMenu>> list(@Validated @RequestBody SysMenu menu) {
        List<SysMenu> menus = menuService.selectMenuList(menu, SecurityUtils.getUserId());
        return JsonResult.success(menus);
    }

    @ApiOperation(value = "根据菜单编号获取详细信息")
    @ApiImplicitParam(name = "menuId", value = "菜单ID", required = true, dataType = "Long", paramType = "path")
    @PreAuthorize("@ss.hasPermi('system:menu:query')")
    @GetMapping(value = "/{menuId}")
    public JsonResult<SysMenu> getInfo(@PathVariable Long menuId) {
        return JsonResult.success(menuService.selectMenuById(menuId));
    }

    @ApiOperation(value = "获取菜单下拉树列表")
    @ApiImplicitParam(name = "menu", value = "菜单信息对象", required = true, dataType = "SysMenu", paramType = "body")
    @GetMapping("/treeSelect")
    public JsonResult<List<TreeSelect>> treeSelect(@Validated @RequestBody SysMenu menu) {
        List<SysMenu> menus = menuService.selectMenuList(menu, SecurityUtils.getUserId());
        return JsonResult.success(menuService.buildMenuTreeSelect(menus));
    }

    @ApiOperation(value = "加载对应角色菜单列表树")
    @ApiImplicitParam(name = "roleId", value = "角色ID", required = true, dataType = "Long", paramType = "path")
    @GetMapping(value = "/roleMenuTreeSelect/{roleId}")
    public JsonResult<Map<String, Object>> roleMenuTreeSelect(@PathVariable("roleId") Long roleId) {
        List<SysMenu> menus = menuService.selectMenuList(SecurityUtils.getUserId());
        Map<String,Object> result = new HashMap<>();
        result.put("checkedKeys", menuService.selectMenuListByRoleId(roleId));
        result.put("menus", menuService.buildMenuTreeSelect(menus));
        return JsonResult.success(result);
    }

    @ApiOperation(value = "新增菜单")
    @ApiImplicitParam(name = "menu", value = "菜单信息对象", required = true, dataType = "SysMenu", paramType = "body")
    @PreAuthorize("@ss.hasPermi('system:menu:add')")
    @OptLog(title = "菜单管理", businessType = BusinessType.INSERT)
    @PostMapping
    public JsonResult<String> add(@Validated @RequestBody SysMenu menu) {
        if (UserConstants.NOT_UNIQUE.equals(menuService.checkMenuNameUnique(menu))) {
            return JsonResult.fail("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        } else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtil.isHttp(menu.getPath())) {
            return JsonResult.fail("新增菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        menu.setCreateBy(SecurityUtils.getUsername());
        return menuService.insertMenu(menu) > 0 ? JsonResult.success() : JsonResult.fail("新增菜单失败");
    }

    @ApiOperation(value = "修改菜单")
    @ApiImplicitParam(name = "menu", value = "菜单信息对象", required = true, dataType = "SysMenu", paramType = "body")
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    @OptLog(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public JsonResult<String> edit(@Validated @RequestBody SysMenu menu) {
        if (UserConstants.NOT_UNIQUE.equals(menuService.checkMenuNameUnique(menu))) {
            return JsonResult.fail("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        } else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtil.isHttp(menu.getPath())) {
            return JsonResult.fail("修改菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        } else if (menu.getMenuId().equals(menu.getParentId())) {
            return JsonResult.fail("修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
        }
        menu.setUpdateBy(SecurityUtils.getUsername());
        return menuService.updateMenu(menu) > 0 ? JsonResult.success() : JsonResult.fail("修改菜单信息失败");
    }

    @ApiOperation(value = "删除菜单")
    @ApiImplicitParam(name = "menuId", value = "菜单ID", required = true, dataType = "Long", paramType = "path")
    @PreAuthorize("@ss.hasPermi('system:menu:remove')")
    @OptLog(title = "菜单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{menuId}")
    public JsonResult<String> remove(@PathVariable("menuId") Long menuId) {
        if (menuService.hasChildByMenuId(menuId)) {
            return JsonResult.fail("存在子菜单,不允许删除");
        }
        if (menuService.checkMenuExistRole(menuId)) {
            return JsonResult.fail("菜单已分配,不允许删除");
        }
        return menuService.deleteMenuById(menuId) > 0 ? JsonResult.success() : JsonResult.fail("删除菜单失败");
    }
}

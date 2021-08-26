package com.asura.admin.controller.system;

import cn.hutool.core.util.ArrayUtil;
import com.asura.admin.common.annotation.OptLog;
import com.asura.admin.common.constant.UserConstants;
import com.asura.admin.common.core.result.JsonResult;
import com.asura.admin.common.enums.BusinessType;
import com.asura.admin.domain.TreeSelect;
import com.asura.admin.entity.SysDept;
import com.asura.admin.service.SysDeptService;
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
 * 部门表 前端控制器
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Api(value = "部门信息控制器")
@RestController
@RequestMapping("/system/dept")
public class SysDeptController {
    @Autowired
    private SysDeptService deptService;

    @ApiOperation(value = "获取部门列表")
    @ApiImplicitParam(name = "dept", value = "部门信息", required = true, dataType = "SysDept", paramType = "body")
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping("/list")
    public JsonResult<List<SysDept>> list(@Validated @RequestBody SysDept dept) {
        List<SysDept> deptList = deptService.selectDeptList(dept);
        return JsonResult.success(deptList);
    }

    @ApiOperation(value = "查询部门列表（排除节点）")
    @ApiImplicitParam(name = "deptId", value = "部门信息ID", dataType = "Long", paramType = "path")
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping("/list/exclude/{deptId}")
    public JsonResult<List<SysDept>> excludeChild(@PathVariable(value = "deptId", required = false) Long deptId) {
        List<SysDept> deptList = deptService.selectDeptList(new SysDept());
        deptList.removeIf(d -> d.getDeptId().intValue() == deptId
                || ArrayUtil.contains(StringUtil.split(d.getAncestors(), ","), deptId + ""));
        return JsonResult.success(deptList);
    }

    @ApiOperation(value = "根据部门编号获取详细信息")
    @ApiImplicitParam(name = "deptId", value = "部门信息ID", required = true, dataType = "Long", paramType = "path")
    @PreAuthorize("@ss.hasPermi('system:dept:query')")
    @GetMapping(value = "/{deptId}")
    public JsonResult<SysDept> getInfo(@PathVariable Long deptId) {
        return JsonResult.success(deptService.selectDeptById(deptId));
    }

    @ApiOperation(value = "获取部门下拉树列表")
    @ApiImplicitParam(name = "dept", value = "部门信息", required = true, dataType = "SysDept", paramType = "body")
    @GetMapping("/treeSelect")
    public JsonResult<List<TreeSelect>> treeSelect(@Validated @RequestBody SysDept dept) {
        List<SysDept> deptList = deptService.selectDeptList(dept);
        return JsonResult.success(deptService.buildDeptTreeSelect(deptList));
    }

    @ApiOperation(value = "加载对应角色部门列表树")
    @ApiImplicitParam(name = "roleId", value = "角色信息ID", required = true, dataType = "Long", paramType = "path")
    @GetMapping(value = "/roleDeptTreeSelect/{roleId}")
    public JsonResult<Map<String,Object>> roleDeptTreeSelect(@PathVariable("roleId") Long roleId) {
        List<SysDept> deptList = deptService.selectDeptList(new SysDept());
        Map<String,Object> result = new HashMap<>();
        result.put("checkedKeys", deptService.selectDeptListByRoleId(roleId));
        result.put("deptList", deptService.buildDeptTreeSelect(deptList));
        return JsonResult.success(result);
    }

    @ApiOperation(value = "新增部门信息")
    @ApiImplicitParam(name = "dept", value = "部门信息", required = true, dataType = "SysDept", paramType = "body")
    @PreAuthorize("@ss.hasPermi('system:dept:add')")
    @OptLog(title = "部门管理", businessType = BusinessType.INSERT)
    @PostMapping
    public JsonResult<String> add(@Validated @RequestBody SysDept dept) {
        if (UserConstants.NOT_UNIQUE.equals(deptService.checkDeptNameUnique(dept))) {
            return JsonResult.fail("新增部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        dept.setCreateBy(SecurityUtils.getUsername());
        return deptService.insertDept(dept) > 0 ? JsonResult.success() : JsonResult.fail("新增部门信息失败");
    }

    @ApiOperation(value = "修改部门信息")
    @ApiImplicitParam(name = "dept", value = "部门信息", required = true, dataType = "SysDept", paramType = "body")
    @PreAuthorize("@ss.hasPermi('system:dept:edit')")
    @OptLog(title = "部门管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public JsonResult<String> edit(@Validated @RequestBody SysDept dept) {
        if (UserConstants.NOT_UNIQUE.equals(deptService.checkDeptNameUnique(dept))) {
            return JsonResult.fail("修改部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        } else if (dept.getParentId().equals(dept.getDeptId())) {
            return JsonResult.fail("修改部门'" + dept.getDeptName() + "'失败，上级部门不能是自己");
        } else if (StringUtil.equals(UserConstants.DEPT_DISABLE, dept.getStatus())
                && deptService.selectNormalChildrenDeptById(dept.getDeptId()) > 0) {
            return JsonResult.fail("该部门包含未停用的子部门！");
        }
        dept.setUpdateBy(SecurityUtils.getUsername());
        return deptService.updateDept(dept) > 0 ? JsonResult.success() : JsonResult.fail("修改部门信息失败");
    }

    @ApiOperation(value = "删除部门")
    @ApiImplicitParam(name = "deptId", value = "部门信息ID", required = true, dataType = "Long", paramType = "path")
    @PreAuthorize("@ss.hasPermi('system:dept:remove')")
    @OptLog(title = "部门管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deptId}")
    public JsonResult<String> remove(@PathVariable Long deptId) {
        if (deptService.hasChildByDeptId(deptId)) {
            return JsonResult.fail("存在下级部门,不允许删除");
        }
        if (deptService.checkDeptExistUser(deptId)) {
            return JsonResult.fail("部门存在用户,不允许删除");
        }
        return deptService.deleteDeptById(deptId) > 0 ? JsonResult.success() : JsonResult.fail("删除部门信息失败");
    }
}

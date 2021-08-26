package com.asura.admin.service.impl;

import com.asura.admin.common.constant.UserConstants;
import com.asura.admin.domain.TreeSelect;
import com.asura.admin.entity.SysDept;
import com.asura.admin.entity.SysRole;
import com.asura.admin.entity.SysUser;
import com.asura.admin.exception.BusinessException;
import com.asura.admin.mapper.SysDeptMapper;
import com.asura.admin.mapper.SysRoleMapper;
import com.asura.admin.mapper.SysUserMapper;
import com.asura.admin.service.SysDeptService;
import com.asura.admin.util.StringUtil;
import com.asura.admin.util.text.Convert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 部门表 服务实现类
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {
    @Autowired
    private SysDeptMapper deptMapper;
    @Autowired
    private SysRoleMapper roleMapper;
    @Autowired
    private SysUserMapper userMapper;

    /**
     * 查询部门管理数据
     * @param dept 部门信息
     * @return 部门信息集合
     */
    @Override
    public List<SysDept> selectDeptList(SysDept dept) {
        return deptMapper.selectDeptList(dept);
    }

    /**
     * 构建前端所需要树结构
     * @param deptList 部门列表
     * @return 树结构列表
     */
    @Override
    public List<SysDept> buildDeptTree(List<SysDept> deptList) {
        List<SysDept> returnList = new ArrayList<>();
        List<Long> tempList = new ArrayList<>();
        for (SysDept dept : deptList) {
            tempList.add(dept.getDeptId());
        }
        for (SysDept dept : deptList) {
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(dept.getParentId())) {
                recursionFn(deptList, dept);
                returnList.add(dept);
            }
        }
        if (returnList.isEmpty()) {
            returnList = deptList;
        }
        return returnList;
    }

    /**
     * 构建前端所需要下拉树结构
     * @param deptList 部门列表
     * @return 下拉树结构列表
     */
    @Override
    public List<TreeSelect> buildDeptTreeSelect(List<SysDept> deptList) {
        List<SysDept> deptTrees = buildDeptTree(deptList);
        return deptTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 根据角色ID查询部门树信息
     * @param roleId 角色ID
     * @return 选中部门列表
     */
    @Override
    public List<Long> selectDeptListByRoleId(Long roleId) {
        SysRole role = roleMapper.selectById(roleId);
        return deptMapper.selectDeptListByRoleId(roleId, role.getDeptCheckStrictly());
    }

    /**
     * 根据部门ID查询信息
     * @param deptId 部门ID
     * @return 部门信息
     */
    @Override
    public SysDept selectDeptById(Long deptId) {
        return getById(deptId);
    }

    /**
     * 根据ID查询所有子部门（正常状态）
     * @param deptId 部门ID
     * @return 子部门数
     */
    @Override
    public int selectNormalChildrenDeptById(Long deptId) {
        return count(
                Wrappers.lambdaQuery(SysDept.class)
                        .eq(SysDept::getStatus, 0)
                        .apply("find_in_set({0}, ancestors)", deptId));
    }

    /**
     * 是否存在子节点
     * @param deptId 部门ID
     * @return 结果
     */
    @Override
    public boolean hasChildByDeptId(Long deptId) {
        int result = count(
                Wrappers.lambdaQuery(SysDept.class)
                        .eq(SysDept::getParentId, deptId)
                        .last("limit 1"));
        return result > 0;
    }

    /**
     * 查询部门是否存在用户
     * @param deptId 部门ID
     * @return 结果 true 存在 false 不存在
     */
    @Override
    public boolean checkDeptExistUser(Long deptId) {
        int result = userMapper.selectCount(Wrappers.lambdaQuery(SysUser.class).eq(SysUser::getDeptId, deptId));
        return result > 0;
    }

    /**
     * 校验部门名称是否唯一
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public String checkDeptNameUnique(SysDept dept) {
        Long deptId = StringUtil.isNull(dept.getDeptId()) ? -1L : dept.getDeptId();
        SysDept info = getOne(
                Wrappers.lambdaQuery(SysDept.class)
                        .eq(SysDept::getDeptName, dept.getDeptName())
                        .eq(SysDept::getParentId, dept.getParentId())
                        .last("limit 1"));
        if (StringUtil.isNotNull(info) && info.getDeptId().longValue() != deptId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 新增保存部门信息
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public int insertDept(SysDept dept) {
        SysDept info = getById(dept.getParentId());
        // 如果父节点不为正常状态,则不允许新增子节点
        if (!UserConstants.DEPT_NORMAL.equals(info.getStatus())) {
            throw new BusinessException("部门停用，不允许新增");
        }
        dept.setAncestors(info.getAncestors() + "," + dept.getParentId());
        return deptMapper.insert(dept);
    }

    /**
     * 修改保存部门信息
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public int updateDept(SysDept dept) {
        SysDept newParentDept = getById(dept.getParentId());
        SysDept oldDept = getById(dept.getDeptId());
        if (StringUtil.isNotNull(newParentDept) && StringUtil.isNotNull(oldDept)) {
            String newAncestors = newParentDept.getAncestors() + "," + newParentDept.getDeptId();
            String oldAncestors = oldDept.getAncestors();
            dept.setAncestors(newAncestors);
            updateDeptChildren(dept.getDeptId(), newAncestors, oldAncestors);
        }
        int result = deptMapper.updateById(dept);
        if (UserConstants.DEPT_NORMAL.equals(dept.getStatus()) && StringUtil.isNotEmpty(dept.getAncestors())
                && !StringUtil.equals("0", dept.getAncestors())) {
            // 如果该部门是启用状态，则启用该部门的所有上级部门
            updateParentDeptStatusNormal(dept);
        }
        return result;
    }

    /**
     * 修改该部门的父级部门状态
     * @param dept 当前部门
     */
    private void updateParentDeptStatusNormal(SysDept dept) {
        String ancestors = dept.getAncestors();
        Long[] deptIds = Convert.toLongArray(ancestors);
        update(null, Wrappers.lambdaUpdate(SysDept.class)
                .set(SysDept::getStatus, "0")
                .in(SysDept::getDeptId, Arrays.asList(deptIds)));
    }

    /**
     * 修改子元素关系
     * @param deptId 被修改的部门ID
     * @param newAncestors 新的父ID集合
     * @param oldAncestors 旧的父ID集合
     */
    public void updateDeptChildren(Long deptId, String newAncestors, String oldAncestors) {
        List<SysDept> children = list(Wrappers.lambdaQuery(SysDept.class).apply("find_in_set({0},ancestors)",deptId));
        for (SysDept child : children) {
            child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
        }
        if (children.size() > 0) {
            deptMapper.updateDeptChildren(children);
        }
    }

    /**
     * 删除部门管理信息
     * @param deptId 部门ID
     * @return 结果
     */
    @Override
    public int deleteDeptById(Long deptId) {
        return deptMapper.deleteById(deptId);
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<SysDept> list, SysDept t) {
        // 得到子节点列表
        List<SysDept> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysDept tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysDept> getChildList(List<SysDept> list, SysDept t) {
        List<SysDept> tList = new ArrayList<>();
        for (SysDept n : list) {
            if (StringUtil.isNotNull(n.getParentId()) && n.getParentId().longValue() == t.getDeptId().longValue()) {
                tList.add(n);
            }
        }
        return tList;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysDept> list, SysDept t) {
        return getChildList(list, t).size() > 0;
    }
}

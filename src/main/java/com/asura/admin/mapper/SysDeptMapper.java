package com.asura.admin.mapper;

import com.asura.admin.entity.SysDept;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 部门表 Mapper 接口
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Mapper
public interface SysDeptMapper extends BaseMapper<SysDept> {
    /**
     * 查询部门管理数据
     * @param dept 部门信息
     * @return 部门信息集合
     */
    List<SysDept> selectDeptList(SysDept dept);

    /**
     * 根据角色ID查询部门树信息
     * @param roleId 角色ID
     * @param deptCheckStrictly 部门树选择项是否关联显示
     * @return 选中部门列表
     */
    List<Long> selectDeptListByRoleId(@Param("roleId") Long roleId, @Param("deptCheckStrictly") boolean deptCheckStrictly);

    /**
     * 修改子元素关系
     * @param deptList 子元素
     * @return 结果
     */
    int updateDeptChildren(@Param("deptList") List<SysDept> deptList);
}

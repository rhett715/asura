package com.asura.admin.mapper;

import com.asura.admin.entity.SysRoleDept;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 角色和部门关联表 Mapper 接口
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Mapper
public interface SysRoleDeptMapper extends BaseMapper<SysRoleDept> {
    /**
     * 批量新增角色部门信息
     * @param roleDeptList 角色部门列表
     * @return 结果
     */
    int insertBatchRoleDept(List<SysRoleDept> roleDeptList);
}

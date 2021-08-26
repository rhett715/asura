package com.asura.admin.mapper;

import com.asura.admin.entity.SysRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 角色信息表 Mapper 接口
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {
    /**
     * 根据条件分页查询角色数据列表
     * @param page 分页信息
     * @param role 角色信息
     * @return 角色数据集合
     */
    Page<SysRole> selectPageRoleList(@Param("page") Page<SysRole> page, @Param("role") SysRole role);

    /**
     * 根据条件查询角色数据列表
     * @param role 角色信息
     * @return 角色数据集合
     */
    List<SysRole> selectRoleList(SysRole role);

    /**
     * 根据用户ID查询角色
     * @param userId 用户ID
     * @return 角色列表
     */
    List<SysRole> selectRolePermissionByUserId(Long userId);


    /**
     * 根据用户ID获取角色选择框列表
     * @param userId 用户ID
     * @return 选中角色ID列表
     */
    List<Long> selectRoleListByUserId(Long userId);

    /**
     * 根据用户ID查询角色
     * @param userName 用户名
     * @return 角色列表
     */
    List<SysRole> selectRolesByUserName(String userName);
}

package com.asura.admin.mapper;

import com.asura.admin.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户信息表 Mapper 接口
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    /**
     * 根据条件分页查询用户列表
     * @param page 分页信息
     * @param user 用户信息
     * @return 用户信息集合
     */
    Page<SysUser> selectPageUserList(@Param("page") Page<SysUser> page, @Param("user") SysUser user);

    /**
     * 根据条件查询用户列表
     * @param sysUser 用户信息
     * @return 用户信息集合
     */
    List<SysUser> selectUserList(SysUser sysUser);

    /**
     * 根据条件分页查询已配用户角色列表
     * @param page 分页信息
     * @param user 用户信息
     * @return 用户信息集合
     */
    Page<SysUser> selectAllocatedList(@Param("page") Page<SysUser> page, @Param("user") SysUser user);

    /**
     * 根据条件分页查询未分配用户角色列表
     * @param page 分页信息
     * @param user 用户信息
     * @return 用户信息集合
     */
    Page<SysUser> selectUnallocatedList(@Param("page") Page<SysUser> page, @Param("user") SysUser user);

    /**
     * 通过用户名查询用户
     * @param userName 用户名
     * @return 用户对象信息
     */
    SysUser selectUserByUserName(String userName);

    /**
     * 通过用户ID查询用户
     * @param userId 用户ID
     * @return 用户对象信息
     */
    SysUser selectUserById(Long userId);
}

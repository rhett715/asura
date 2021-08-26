package com.asura.admin.mapper;

import com.asura.admin.entity.SysMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 菜单权限表 Mapper 接口
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {
    /**
     * 根据用户所有权限
     * @return 权限列表
     */
    List<String> selectMenuPerms();

    /**
     * 根据用户查询系统菜单列表
     * @param menu 菜单信息
     * @return 菜单列表
     */
    List<SysMenu> selectMenuListByUserId(SysMenu menu);

    /**
     * 根据用户ID查询权限
     * @param userId 用户ID
     * @return 权限列表
     */
    List<String> selectMenuPermsByUserId(Long userId);

    /**
     * 根据用户ID查询菜单
     * @return 菜单列表
     */
    List<SysMenu> selectMenuTreeAll();

    /**
     * 根据用户ID查询菜单
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenu> selectMenuTreeByUserId(Long userId);

    /**
     * 根据角色ID查询菜单树信息
     * @param roleId 角色ID
     * @param menuCheckStrictly 菜单树选择项是否关联显示
     * @return 选中菜单列表
     */
    List<Long> selectMenuListByRoleId(@Param("roleId") Long roleId, @Param("menuCheckStrictly") boolean menuCheckStrictly);
}

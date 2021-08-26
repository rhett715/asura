package com.asura.admin.mapper;

import com.asura.admin.entity.SysRoleMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 角色和菜单关联表 Mapper 接口
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {
    /**
     * 批量新增角色菜单信息
     * @param roleMenuList 角色菜单列表
     * @return 结果
     */
    int insertBatchRoleMenu(List<SysRoleMenu> roleMenuList);
}

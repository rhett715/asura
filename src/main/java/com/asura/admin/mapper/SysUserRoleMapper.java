package com.asura.admin.mapper;

import com.asura.admin.entity.SysUserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 用户和角色关联表 Mapper 接口
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
    /**
     * 批量新增用户角色信息
     * @param userRoleList 用户角色列表
     * @return 结果
     */
    int insertBatchUserRole(List<SysUserRole> userRoleList);
}

package com.asura.admin.mapper;

import com.asura.admin.entity.SysUserPost;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 用户与岗位关联表 Mapper 接口
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Mapper
public interface SysUserPostMapper extends BaseMapper<SysUserPost> {
    /**
     * 批量新增用户岗位信息
     * @param userPostList 用户角色列表
     * @return 结果
     */
    int insertBatchUserPost(List<SysUserPost> userPostList);
}

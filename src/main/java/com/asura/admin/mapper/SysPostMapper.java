package com.asura.admin.mapper;

import com.asura.admin.entity.SysPost;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 岗位信息表 Mapper 接口
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Mapper
public interface SysPostMapper extends BaseMapper<SysPost> {
    /**
     * 根据用户ID获取岗位选择框列表
     * @param userId 用户ID
     * @return 选中岗位ID列表
     */
    List<Long> selectPostListByUserId(Long userId);

    /**
     * 查询用户所属岗位组
     * @param userName 用户名
     * @return 岗位信息列表
     */
    List<SysPost> selectPostsByUserName(String userName);
}

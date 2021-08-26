package com.asura.admin.service;

import com.asura.admin.common.core.page.TableDataInfo;
import com.asura.admin.entity.SysPost;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 岗位信息表 服务类
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
public interface SysPostService extends IService<SysPost> {
    /**
     * 按条件分页查询岗位信息集合
     * @param post 岗位信息
     * @return 岗位列表分页信息
     */
    TableDataInfo<SysPost> selectPagePostList(SysPost post);

    /**
     * 查询岗位信息集合
     * @param post 岗位信息
     * @return 岗位列表
     */
    List<SysPost> selectPostList(SysPost post);

    /**
     * 查询所有岗位
     * @return 岗位列表
     */
    List<SysPost> selectPostAll();

    /**
     * 通过岗位ID查询岗位信息
     * @param postId 岗位ID
     * @return 角色对象信息
     */
    SysPost selectPostById(Long postId);

    /**
     * 根据用户ID获取岗位选择框列表
     * @param userId 用户ID
     * @return 选中岗位ID列表
     */
    List<Long> selectPostListByUserId(Long userId);

    /**
     * 校验岗位名称
     * @param post 岗位信息
     * @return 结果
     */
    String checkPostNameUnique(SysPost post);

    /**
     * 校验岗位编码
     * @param post 岗位信息
     * @return 结果
     */
    String checkPostCodeUnique(SysPost post);

    /**
     * 通过岗位ID查询岗位使用数量
     * @param postId 岗位ID
     * @return 结果
     */
    int countUserPostById(Long postId);

    /**
     * 删除岗位信息
     * @param postId 岗位ID
     * @return 结果
     */
    int deletePostById(Long postId);

    /**
     * 批量删除岗位信息
     * @param postIds 需要删除的岗位ID
     * @return 结果
     */
    int deletePostByIds(Long[] postIds);

    /**
     * 新增保存岗位信息
     * @param post 岗位信息
     * @return 结果
     */
    int insertPost(SysPost post);

    /**
     * 修改保存岗位信息
     * @param post 岗位信息
     * @return 结果
     */
    int updatePost(SysPost post);
}

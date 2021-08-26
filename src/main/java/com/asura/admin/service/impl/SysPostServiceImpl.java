package com.asura.admin.service.impl;

import com.asura.admin.common.constant.UserConstants;
import com.asura.admin.common.core.page.TableDataInfo;
import com.asura.admin.entity.SysPost;
import com.asura.admin.entity.SysUserPost;
import com.asura.admin.exception.BusinessException;
import com.asura.admin.mapper.SysPostMapper;
import com.asura.admin.mapper.SysUserPostMapper;
import com.asura.admin.service.SysPostService;
import com.asura.admin.util.PageUtils;
import com.asura.admin.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 岗位信息表 服务实现类
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Service
public class SysPostServiceImpl extends ServiceImpl<SysPostMapper, SysPost> implements SysPostService {
    @Autowired
    private SysPostMapper postMapper;
    @Autowired
    private SysUserPostMapper userPostMapper;

    /**
     * 按条件分页查询岗位信息集合
     * @param post 岗位信息
     * @return 岗位列表分页信息
     */
    @Override
    public TableDataInfo<SysPost> selectPagePostList(SysPost post) {
        LambdaQueryWrapper<SysPost> wrapper = Wrappers.lambdaQuery(SysPost.class)
                .like(StringUtil.isNotBlank(post.getPostCode()), SysPost::getPostCode, post.getPostCode())
                .eq(StringUtil.isNotBlank(post.getStatus()), SysPost::getStatus, post.getStatus())
                .like(StringUtil.isNotBlank(post.getPostName()), SysPost::getPostName, post.getPostName()
                );
        return PageUtils.buildDataInfo(page(PageUtils.buildPage(),wrapper));
    }

    /**
     * 查询岗位信息集合
     * @param post 岗位信息
     * @return 岗位信息集合
     */
    @Override
    public List<SysPost> selectPostList(SysPost post) {
        return postMapper.selectList(
                Wrappers.lambdaQuery(SysPost.class)
                        .like(StringUtil.isNotBlank(post.getPostCode()), SysPost::getPostCode, post.getPostCode())
                        .eq(StringUtil.isNotBlank(post.getStatus()), SysPost::getStatus, post.getStatus())
                        .like(StringUtil.isNotBlank(post.getPostName()), SysPost::getPostName, post.getPostName())
        );
    }

    /**
     * 查询所有岗位
     * @return 岗位列表
     */
    @Override
    public List<SysPost> selectPostAll() {
        return postMapper.selectList(Wrappers.emptyWrapper());
    }

    /**
     * 通过岗位ID查询岗位信息
     * @param postId 岗位ID
     * @return 角色对象信息
     */
    @Override
    public SysPost selectPostById(Long postId) {
        return postMapper.selectById(postId);
    }

    /**
     * 根据用户ID获取岗位选择框列表
     * @param userId 用户ID
     * @return 选中岗位ID列表
     */
    @Override
    public List<Long> selectPostListByUserId(Long userId) {
        return postMapper.selectPostListByUserId(userId);
    }

    /**
     * 校验岗位名称是否唯一
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public String checkPostNameUnique(SysPost post) {
        Long postId = StringUtil.isNull(post.getPostId()) ? -1L : post.getPostId();
        SysPost info = getOne(Wrappers.lambdaQuery(SysPost.class)
                .eq(SysPost::getPostName, post.getPostName())
                .last("limit 1")
        );
        if (StringUtil.isNotNull(info) && info.getPostId().longValue() != postId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验岗位编码是否唯一
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public String checkPostCodeUnique(SysPost post) {
        Long postId = StringUtil.isNull(post.getPostId()) ? -1L : post.getPostId();
        SysPost info = getOne(Wrappers.lambdaQuery(SysPost.class)
                .eq(SysPost::getPostCode, post.getPostCode())
                .last("limit 1")
        );
        if (StringUtil.isNotNull(info) && info.getPostId().longValue() != postId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 通过岗位ID查询岗位使用数量
     * @param postId 岗位ID
     * @return 结果
     */
    @Override
    public int countUserPostById(Long postId) {
        return userPostMapper.selectCount(Wrappers.lambdaQuery(SysUserPost.class).eq(SysUserPost::getPostId,postId));
    }

    /**
     * 删除岗位信息
     * @param postId 岗位ID
     * @return 结果
     */
    @Override
    public int deletePostById(Long postId) {
        return postMapper.deleteById(postId);
    }

    /**
     * 批量删除岗位信息
     * @param postIds 需要删除的岗位ID
     * @return 结果
     */
    @Override
    public int deletePostByIds(Long[] postIds) {
        for (Long postId : postIds) {
            SysPost post = selectPostById(postId);
            if (countUserPostById(postId) > 0) {
                throw new BusinessException(String.format("%1$s已分配,不能删除", post.getPostName()));
            }
        }
        return postMapper.deleteBatchIds(Arrays.asList(postIds));
    }

    /**
     * 新增保存岗位信息
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public int insertPost(SysPost post) {
        return postMapper.insert(post);
    }

    /**
     * 修改保存岗位信息
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public int updatePost(SysPost post) {
        return postMapper.updateById(post);
    }
}

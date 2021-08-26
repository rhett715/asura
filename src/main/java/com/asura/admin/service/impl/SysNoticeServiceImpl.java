package com.asura.admin.service.impl;

import com.asura.admin.common.core.page.TableDataInfo;
import com.asura.admin.entity.SysNotice;
import com.asura.admin.mapper.SysNoticeMapper;
import com.asura.admin.service.SysNoticeService;
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
 * 通知公告表 服务实现类
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Service
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNotice> implements SysNoticeService {
    @Autowired
    private SysNoticeMapper noticeMapper;

    /**
     * 按照条件分页查询公告信息
     * @param notice 公告信息
     * @return 公告信息分页列表
     */
    @Override
    public TableDataInfo<SysNotice> selectPageNoticeList(SysNotice notice) {
        LambdaQueryWrapper<SysNotice> wrapper = Wrappers.lambdaQuery(SysNotice.class)
                .like(StringUtil.isNotBlank(notice.getNoticeTitle()), SysNotice::getNoticeTitle, notice.getNoticeTitle())
                .eq(StringUtil.isNotBlank(notice.getNoticeType()), SysNotice::getNoticeType, notice.getNoticeType())
                .like(StringUtil.isNotBlank(notice.getCreateBy()), SysNotice::getCreateBy, notice.getCreateBy());
        return PageUtils.buildDataInfo(page(PageUtils.buildPage(), wrapper));
    }

    /**
     * 查询公告信息
     * @param noticeId 公告ID
     * @return 公告信息
     */
    @Override
    public SysNotice selectNoticeById(Long noticeId) {
        return noticeMapper.selectById(noticeId);
    }

    /**
     * 查询公告列表
     * @param notice 公告信息
     * @return 公告集合
     */
    @Override
    public List<SysNotice> selectNoticeList(SysNotice notice) {
        return noticeMapper.selectList(
                Wrappers.lambdaQuery(SysNotice.class)
                        .like(StringUtil.isNotBlank(notice.getNoticeTitle()),SysNotice::getNoticeTitle,notice.getNoticeTitle())
                        .eq(StringUtil.isNotBlank(notice.getNoticeType()),SysNotice::getNoticeType,notice.getNoticeType())
                        .like(StringUtil.isNotBlank(notice.getCreateBy()),SysNotice::getCreateBy,notice.getCreateBy())
        );
    }

    /**
     * 新增公告
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int insertNotice(SysNotice notice) {
        return noticeMapper.insert(notice);
    }

    /**
     * 修改公告
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int updateNotice(SysNotice notice) {
        return noticeMapper.updateById(notice);
    }

    /**
     * 删除公告对象
     * @param noticeId 公告ID
     * @return 结果
     */
    @Override
    public int deleteNoticeById(Long noticeId) {
        return noticeMapper.deleteById(noticeId);
    }

    /**
     * 批量删除公告信息
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    @Override
    public int deleteNoticeByIds(Long[] noticeIds) {
        return noticeMapper.deleteBatchIds(Arrays.asList(noticeIds));
    }
}

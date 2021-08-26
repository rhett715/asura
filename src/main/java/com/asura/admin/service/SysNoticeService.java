package com.asura.admin.service;

import com.asura.admin.common.core.page.TableDataInfo;
import com.asura.admin.entity.SysNotice;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 通知公告表 服务类
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
public interface SysNoticeService extends IService<SysNotice> {
    /**
     * 按照条件分页查询公告信息
     * @param notice 公告信息
     * @return 公告信息分页列表
     */
    TableDataInfo<SysNotice> selectPageNoticeList(SysNotice notice);

    /**
     * 查询公告信息
     * @param noticeId 公告ID
     * @return 公告信息
     */
    SysNotice selectNoticeById(Long noticeId);

    /**
     * 查询公告列表
     * @param notice 公告信息
     * @return 公告集合
     */
    List<SysNotice> selectNoticeList(SysNotice notice);

    /**
     * 新增公告
     * @param notice 公告信息
     * @return 结果
     */
    int insertNotice(SysNotice notice);

    /**
     * 修改公告
     * @param notice 公告信息
     * @return 结果
     */
    int updateNotice(SysNotice notice);

    /**
     * 删除公告信息
     * @param noticeId 公告ID
     * @return 结果
     */
    int deleteNoticeById(Long noticeId);

    /**
     * 批量删除公告信息
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    int deleteNoticeByIds(Long[] noticeIds);
}

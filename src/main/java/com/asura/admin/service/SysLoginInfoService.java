package com.asura.admin.service;

import com.asura.admin.common.core.page.TableDataInfo;
import com.asura.admin.entity.SysLoginInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 系统访问记录 服务类
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
public interface SysLoginInfoService extends IService<SysLoginInfo> {
    /**
     * 分页查询系统登录信息
     * @param loginInfo 访问日志对象
     * @return 访问日志对象分页信息
     */
    TableDataInfo<SysLoginInfo> selectPageLoginInfoList(SysLoginInfo loginInfo);

    /**
     * 新增系统登录日志
     * @param loginInfo 访问日志对象
     */
    void insertLoginInfo(SysLoginInfo loginInfo);

    /**
     * 查询系统登录日志集合
     * @param loginInfo 访问日志对象
     * @return 登录记录集合
     */
    List<SysLoginInfo> selectLoginInfoList(SysLoginInfo loginInfo);

    /**
     * 批量删除系统登录日志
     * @param infoIds 需要删除的登录日志ID
     * @return 结果
     */
    int deleteLoginInfoByIds(Long[] infoIds);

    /**
     * 清空系统登录日志
     */
    void cleanLoginInfo();
}

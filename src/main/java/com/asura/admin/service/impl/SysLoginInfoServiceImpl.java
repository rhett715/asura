package com.asura.admin.service.impl;

import com.asura.admin.common.core.page.TableDataInfo;
import com.asura.admin.entity.SysLoginInfo;
import com.asura.admin.exception.BusinessException;
import com.asura.admin.mapper.SysLoginInfoMapper;
import com.asura.admin.service.SysLoginInfoService;
import com.asura.admin.util.PageUtils;
import com.asura.admin.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 系统访问记录 服务实现类
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Service
public class SysLoginInfoServiceImpl extends ServiceImpl<SysLoginInfoMapper, SysLoginInfo> implements SysLoginInfoService {
    @Autowired
    private SysLoginInfoMapper loginInfoMapper;

    /**
     * 分页查询系统登录信息
     * @param loginInfo 访问日志对象
     * @return 访问日志对象分页信息
     */
    @Override
    public TableDataInfo<SysLoginInfo> selectPageLoginInfoList(SysLoginInfo loginInfo) {
        Map<String, Object> params = loginInfo.getParams();
        LambdaQueryWrapper<SysLoginInfo> wrapper = Wrappers.lambdaQuery(SysLoginInfo.class)
                .like(StringUtil.isNotBlank(loginInfo.getIpAddr()), SysLoginInfo::getIpAddr, loginInfo.getIpAddr())
                .eq(StringUtil.isNotBlank(loginInfo.getStatus()), SysLoginInfo::getStatus, loginInfo.getStatus())
                .like(StringUtil.isNotBlank(loginInfo.getUserName()), SysLoginInfo::getUserName, loginInfo.getUserName())
                .apply(StringUtil.isNotEmpty(params.get("beginTime")),
                        "date_format(login_time,'%y%m%d') >= date_format({0},'%y%m%d')",
                        params.get("beginTime"))
                .apply(StringUtil.isNotEmpty(params.get("endTime")),
                        "date_format(login_time,'%y%m%d') <= date_format({0},'%y%m%d')",
                        params.get("endTime"));
        return PageUtils.buildDataInfo(page(PageUtils.buildPage("info_id","desc"), wrapper));
    }

    /**
     * 新增系统登录日志
     * @param loginInfo 访问日志对象
     */
    @Override
    @Transactional
    public void insertLoginInfo(SysLoginInfo loginInfo) {
        loginInfo.setLoginTime(new Date());
        //save(loginInfo);
        boolean retBool = SqlHelper.retBool(loginInfoMapper.insert(loginInfo));
        if (!retBool) {
            throw new BusinessException("新增系统登录日志出现错误！");
        }
    }

    /**
     * 查询系统登录日志集合
     * @param loginInfo 访问日志对象
     * @return 登录记录集合
     */
    @Override
    public List<SysLoginInfo> selectLoginInfoList(SysLoginInfo loginInfo) {
        Map<String, Object> params = loginInfo.getParams();
        return loginInfoMapper.selectList(
                Wrappers.lambdaQuery(SysLoginInfo.class)
                        .like(StringUtil.isNotBlank(loginInfo.getIpAddr()),SysLoginInfo::getIpAddr,loginInfo.getIpAddr())
                        .eq(StringUtil.isNotBlank(loginInfo.getStatus()),SysLoginInfo::getStatus,loginInfo.getStatus())
                        .like(StringUtil.isNotBlank(loginInfo.getUserName()),SysLoginInfo::getUserName,loginInfo.getUserName())
                        .apply(StringUtil.isNotEmpty(params.get("beginTime")),
                                "date_format(login_time,'%y%m%d') >= date_format({0},'%y%m%d')",
                                params.get("beginTime"))
                        .apply(StringUtil.isNotEmpty(params.get("endTime")),
                                "date_format(login_time,'%y%m%d') <= date_format({0},'%y%m%d')",
                                params.get("endTime"))
                        .orderByDesc(SysLoginInfo::getInfoId));
    }

    /**
     * 批量删除系统登录日志
     * @param infoIds 需要删除的登录日志ID
     * @return 受影响记录数
     */
    @Override
    @Transactional
    public int deleteLoginInfoByIds(Long[] infoIds) {
        return loginInfoMapper.deleteBatchIds(Arrays.asList(infoIds));
    }

    /**
     * 清空系统登录日志
     */
    @Override
    public void cleanLoginInfo() {
        remove(Wrappers.emptyWrapper());
    }
}

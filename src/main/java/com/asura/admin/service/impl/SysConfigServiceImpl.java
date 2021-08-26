package com.asura.admin.service.impl;

import com.asura.admin.common.constant.RedisConstants;
import com.asura.admin.common.constant.UserConstants;
import com.asura.admin.common.core.page.TableDataInfo;
import com.asura.admin.entity.SysConfig;
import com.asura.admin.exception.BusinessException;
import com.asura.admin.mapper.SysConfigMapper;
import com.asura.admin.service.SysConfigService;
import com.asura.admin.util.PageUtils;
import com.asura.admin.util.RedisCache;
import com.asura.admin.util.StringUtil;
import com.asura.admin.util.text.Convert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 参数配置表 服务实现类
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private SysConfigMapper configMapper;

    /**
     * 项目启动时，初始化参数到缓存
     */
    @PostConstruct
    public void init() {
        loadingConfigCache();
    }

    /**
     * 分页获取参数配置信息
     * @param config 参数配置信息
     * @return 参数配置信息列表
     */
    @Override
    public TableDataInfo<SysConfig> selectPageConfigList(SysConfig config) {
        Map<String, Object> params = config.getParams();
        LambdaQueryWrapper<SysConfig> lqw = Wrappers.lambdaQuery(SysConfig.class)
                .like(StringUtil.isNotBlank(config.getConfigName()), SysConfig::getConfigName, config.getConfigName())
                .eq(StringUtil.isNotBlank(config.getConfigType()), SysConfig::getConfigType, config.getConfigType())
                .like(StringUtil.isNotBlank(config.getConfigKey()), SysConfig::getConfigKey, config.getConfigKey())
                .apply(StringUtil.isNotEmpty(params.get("beginTime")),
                        "date_format(create_time,'%y%m%d') >= date_format({0},'%y%m%d')",
                        params.get("beginTime"))
                .apply(StringUtil.isNotEmpty(params.get("endTime")),
                        "date_format(create_time,'%y%m%d') <= date_format({0},'%y%m%d')",
                        params.get("endTime"));
        return PageUtils.buildDataInfo(page(PageUtils.buildPage(), lqw));
    }

    /**
     * 查询参数配置信息
     * @param configId 参数配置ID
     * @return 参数配置信息
     */
    @Override
    public SysConfig selectConfigById(Long configId) {
        return configMapper.selectById(configId);
    }

    /**
     * 根据键名查询参数配置信息
     * @param configKey 参数key
     * @return 参数键值
     */
    @Override
    public String selectConfigByKey(String configKey) {
        String configValue = Convert.toStr(redisCache.getCacheObject(getCacheKey(configKey)));
        if (StringUtil.isNotEmpty(configValue)) {
            return configValue;
        }
        SysConfig retConfig = configMapper.selectOne(Wrappers.lambdaQuery(SysConfig.class)
                .eq(SysConfig::getConfigKey, configKey));
        if (StringUtil.isNotNull(retConfig)) {
            redisCache.setCacheObject(getCacheKey(configKey), retConfig.getConfigValue());
            return retConfig.getConfigValue();
        }
        return StringUtil.EMPTY;
    }

    /**
     * 获取验证码开关
     * @return true开启，false关闭
     */
    @Override
    public boolean selectCaptchaOnOff() {
        String captchaOnOff = selectConfigByKey("sys.account.captchaOnOff");
        if (StringUtil.isEmpty(captchaOnOff)) {
            return true;
        }
        return Convert.toBool(captchaOnOff);
    }

    /**
     * 查询参数配置列表
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    @Override
    public List<SysConfig> selectConfigList(SysConfig config) {
        Map<String, Object> params = config.getParams();
        LambdaQueryWrapper<SysConfig> lqw = Wrappers.lambdaQuery(SysConfig.class)
                .like(StringUtil.isNotBlank(config.getConfigName()), SysConfig::getConfigName, config.getConfigName())
                .eq(StringUtil.isNotBlank(config.getConfigType()), SysConfig::getConfigType, config.getConfigType())
                .like(StringUtil.isNotBlank(config.getConfigKey()), SysConfig::getConfigKey, config.getConfigKey())
                .apply(StringUtil.isNotEmpty(params.get("beginTime")),
                        "date_format(create_time,'%y%m%d') >= date_format({0},'%y%m%d')",
                        params.get("beginTime"))
                .apply(StringUtil.isNotEmpty(params.get("endTime")),
                        "date_format(create_time,'%y%m%d') <= date_format({0},'%y%m%d')",
                        params.get("endTime"));
        return configMapper.selectList(lqw);
    }

    /**
     * 新增参数配置
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int insertConfig(SysConfig config) {
        int row = configMapper.insert(config);
        if (row > 0) {
            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    /**
     * 修改参数配置
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int updateConfig(SysConfig config) {
        int row = configMapper.updateById(config);
        if (row > 0) {
            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    /**
     * 批量删除参数信息
     * @param configIds 需要删除的参数ID
     */
    @Override
    public void deleteConfigByIds(Long[] configIds) {
        for (Long configId : configIds) {
            SysConfig config = selectConfigById(configId);
            if (StringUtil.equals(UserConstants.YES, config.getConfigType())) {
                throw new BusinessException(String.format("内置参数【%1$s】不能删除 ", config.getConfigKey()));
            }
            redisCache.deleteObject(getCacheKey(config.getConfigKey()));
        }
        configMapper.deleteBatchIds(Arrays.asList(configIds));
    }

    /**
     * 加载参数缓存数据
     */
    @Override
    public void loadingConfigCache() {
        List<SysConfig> configsList = selectConfigList(new SysConfig());
        for (SysConfig config : configsList) {
            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
    }

    /**
     * 清空参数缓存数据
     */
    @Override
    public void clearConfigCache() {
        Collection<String> keys = redisCache.keys(RedisConstants.SYS_CONFIG_KEY + "*");
        redisCache.deleteObject(keys);
    }

    /**
     * 重置参数缓存数据
     */
    @Override
    public void resetConfigCache() {
        clearConfigCache();
        loadingConfigCache();
    }

    /**
     * 校验参数键名是否唯一
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public String checkConfigKeyUnique(SysConfig config) {
        long configId = StringUtil.isNull(config.getConfigId()) ? -1L : config.getConfigId();
        SysConfig info = configMapper.selectOne(Wrappers.lambdaQuery(SysConfig.class).eq(SysConfig::getConfigKey, config.getConfigKey()));
        if (StringUtil.isNotNull(info) && info.getConfigId().longValue() != configId) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 设置cache key
     * @param configKey 参数键
     * @return 缓存键key
     */
    private String getCacheKey(String configKey) {
        return RedisConstants.SYS_CONFIG_KEY + configKey;
    }
}

package com.asura.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.asura.admin.common.constant.UserConstants;
import com.asura.admin.common.core.page.TableDataInfo;
import com.asura.admin.entity.SysDictData;
import com.asura.admin.entity.SysDictType;
import com.asura.admin.exception.BusinessException;
import com.asura.admin.mapper.SysDictDataMapper;
import com.asura.admin.mapper.SysDictTypeMapper;
import com.asura.admin.service.SysDictTypeService;
import com.asura.admin.util.DictUtils;
import com.asura.admin.util.PageUtils;
import com.asura.admin.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 字典类型表 服务实现类
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Service
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements SysDictTypeService {
    @Autowired
    private SysDictTypeMapper dictTypeMapper;
    @Autowired
    private SysDictDataMapper dictDataMapper;

    /**
     * 项目启动时，初始化字典到缓存
     */
    @PostConstruct
    public void init() {
        loadingDictCache();
    }

    /**
     * 根据条件分页查询字典类型信息
     * @param dictType 字典类型信息
     * @return 字典类型分页信息
     */
    @Override
    public TableDataInfo<SysDictType> selectPageDictTypeList(SysDictType dictType) {
        Map<String, Object> params = dictType.getParams();
        LambdaQueryWrapper<SysDictType> lqw = Wrappers.lambdaQuery(SysDictType.class)
                .like(StringUtil.isNotBlank(dictType.getDictName()), SysDictType::getDictName, dictType.getDictName())
                .eq(StringUtil.isNotBlank(dictType.getStatus()), SysDictType::getStatus, dictType.getStatus())
                .like(StringUtil.isNotBlank(dictType.getDictType()), SysDictType::getDictType, dictType.getDictType())
                .apply(StringUtil.isNotEmpty(params.get("beginTime")),
                        "date_format(create_time,'%y%m%d') >= date_format({0},'%y%m%d')",
                        params.get("beginTime"))
                .apply(StringUtil.isNotEmpty(params.get("endTime")),
                        "date_format(create_time,'%y%m%d') <= date_format({0},'%y%m%d')",
                        params.get("endTime"));
        return PageUtils.buildDataInfo(page(PageUtils.buildPage(), lqw));
    }

    /**
     * 根据条件分页查询字典类型
     * @param dictType 字典类型信息
     * @return 字典类型集合信息
     */
    @Override
    public List<SysDictType> selectDictTypeList(SysDictType dictType) {
        Map<String, Object> params = dictType.getParams();
        return list(Wrappers.lambdaQuery(SysDictType.class)
                .like(StringUtil.isNotBlank(dictType.getDictName()), SysDictType::getDictName, dictType.getDictName())
                .eq(StringUtil.isNotBlank(dictType.getStatus()), SysDictType::getStatus, dictType.getStatus())
                .like(StringUtil.isNotBlank(dictType.getDictType()), SysDictType::getDictType, dictType.getDictType())
                .apply(StringUtil.isNotEmpty(params.get("beginTime")),
                        "date_format(create_time,'%y%m%d') >= date_format({0},'%y%m%d')",
                        params.get("beginTime"))
                .apply(StringUtil.isNotEmpty(params.get("endTime")),
                        "date_format(create_time,'%y%m%d') <= date_format({0},'%y%m%d')",
                        params.get("endTime")));
    }

    /**
     * 根据所有字典类型
     * @return 字典类型集合信息
     */
    @Override
    public List<SysDictType> selectDictTypeAll() {
        return list();
    }

    /**
     * 根据字典类型查询字典数据
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    @Override
    public List<SysDictData> selectDictDataByType(String dictType) {
        List<SysDictData> dictDataList = DictUtils.getDictCache(dictType);
        if (CollUtil.isNotEmpty(dictDataList)) {
            return dictDataList;
        }
        dictDataList = dictDataMapper.selectDictDataByType(dictType);
        if (CollUtil.isNotEmpty(dictDataList)) {
            DictUtils.setDictCache(dictType, dictDataList);
            return dictDataList;
        }
        return null;
    }

    /**
     * 根据字典类型ID查询信息
     * @param dictId 字典类型ID
     * @return 字典类型
     */
    @Override
    public SysDictType selectDictTypeById(Long dictId) {
        return getById(dictId);
    }

    /**
     * 根据字典类型查询信息
     * @param dictType 字典类型
     * @return 字典类型
     */
    @Override
    public SysDictType selectDictTypeByType(String dictType) {
        return getOne(Wrappers.lambdaQuery(SysDictType.class).eq(SysDictType::getDictType, dictType));
    }

    /**
     * 批量删除字典类型信息
     * @param dictIds 需要删除的字典ID
     */
    @Override
    public void deleteDictTypeByIds(Long[] dictIds) {
        for (Long dictId : dictIds) {
            SysDictType dictType = selectDictTypeById(dictId);
            if (dictDataMapper.selectCount(Wrappers.lambdaQuery(SysDictData.class)
                    .eq(SysDictData::getDictType, dictType.getDictType())) > 0) {
                throw new BusinessException(String.format("%1$s已分配,不能删除", dictType.getDictName()));
            }
            DictUtils.removeDictCache(dictType.getDictType());
        }
        dictTypeMapper.deleteBatchIds(Arrays.asList(dictIds));
    }

    /**
     * 加载字典缓存数据
     */
    @Override
    public void loadingDictCache() {
        List<SysDictType> dictTypeList = list();
        for (SysDictType dictType : dictTypeList) {
            List<SysDictData> dictDataList = dictDataMapper.selectDictDataByType(dictType.getDictType());
            DictUtils.setDictCache(dictType.getDictType(), dictDataList);
        }
    }

    /**
     * 清空字典缓存数据
     */
    @Override
    public void clearDictCache() {
        DictUtils.clearDictCache();
    }

    /**
     * 重置字典缓存数据
     */
    @Override
    public void resetDictCache() {
        clearDictCache();
        loadingDictCache();
    }

    /**
     * 新增保存字典类型信息
     *
     * @param dict 字典类型信息
     * @return 结果
     */
    @Override
    public int insertDictType(SysDictType dict) {
        int row = dictTypeMapper.insert(dict);
        if (row > 0) {
            DictUtils.setDictCache(dict.getDictType(), null);
        }
        return row;
    }

    /**
     * 修改保存字典类型信息
     *
     * @param dict 字典类型信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateDictType(SysDictType dict) {
        SysDictType oldDict = getById(dict.getDictId());
        dictDataMapper.update(null, Wrappers.lambdaUpdate(SysDictData.class)
                .set(SysDictData::getDictType, dict.getDictType())
                .eq(SysDictData::getDictType, oldDict.getDictType()));
        int row = dictTypeMapper.updateById(dict);
        if (row > 0) {
            List<SysDictData> dictDataList = dictDataMapper.selectDictDataByType(dict.getDictType());
            DictUtils.setDictCache(dict.getDictType(), dictDataList);
        }
        return row;
    }

    /**
     * 校验字典类型称是否唯一
     *
     * @param dict 字典类型
     * @return 结果
     */
    @Override
    public String checkDictTypeUnique(SysDictType dict) {
        Long dictId = StringUtil.isNull(dict.getDictId()) ? -1L : dict.getDictId();
        SysDictType dictType = getOne(Wrappers.lambdaQuery(SysDictType.class)
                .eq(SysDictType::getDictType, dict.getDictType())
                .last("limit 1"));
        if (StringUtil.isNotNull(dictType) && dictType.getDictId().longValue() != dictId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }
}
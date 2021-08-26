package com.asura.admin.service.impl;

import com.asura.admin.common.core.page.TableDataInfo;
import com.asura.admin.entity.SysDictData;
import com.asura.admin.mapper.SysDictDataMapper;
import com.asura.admin.service.SysDictDataService;
import com.asura.admin.util.DictUtils;
import com.asura.admin.util.PageUtils;
import com.asura.admin.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 字典数据表 服务实现类
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Service
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData> implements SysDictDataService {
    @Autowired
    private SysDictDataMapper dictDataMapper;

    /**
     * 根据条件分页查询字典数据信息
     * @param dictData 字典数据信息
     * @return 字典数据分页信息
     */
    @Override
    public TableDataInfo<SysDictData> selectPageDictDataList(SysDictData dictData) {
        LambdaQueryWrapper<SysDictData> wrapper = Wrappers.lambdaQuery(SysDictData.class)
                .eq(StringUtil.isNotBlank(dictData.getDictType()), SysDictData::getDictType, dictData.getDictType())
                .like(StringUtil.isNotBlank(dictData.getDictLabel()), SysDictData::getDictLabel, dictData.getDictLabel())
                .eq(StringUtil.isNotBlank(dictData.getStatus()), SysDictData::getStatus, dictData.getStatus())
                .orderByAsc(SysDictData::getDictSort);
        return PageUtils.buildDataInfo(page(PageUtils.buildPage(), wrapper));
    }

    /**
     * 根据条件分页查询字典数据
     * @param dictData 字典数据信息
     * @return 字典数据集合信息
     */
    @Override
    public List<SysDictData> selectDictDataList(SysDictData dictData) {
        return list(Wrappers.lambdaQuery(SysDictData.class)
                .eq(StringUtil.isNotBlank(dictData.getDictType()), SysDictData::getDictType, dictData.getDictType())
                .like(StringUtil.isNotBlank(dictData.getDictLabel()), SysDictData::getDictLabel, dictData.getDictLabel())
                .eq(StringUtil.isNotBlank(dictData.getStatus()), SysDictData::getStatus, dictData.getStatus())
                .orderByAsc(SysDictData::getDictSort));
    }

    /**
     * 根据字典类型和字典键值查询字典数据信息
     * @param dictType  字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    @Override
    public String selectDictLabel(String dictType, String dictValue) {
        return getOne(Wrappers.lambdaQuery(SysDictData.class)
                .select(SysDictData::getDictLabel)
                .eq(SysDictData::getDictType, dictType)
                .eq(SysDictData::getDictValue, dictValue))
                .getDictLabel();
    }

    /**
     * 根据字典数据ID查询信息
     * @param dictCode 字典数据ID
     * @return 字典数据
     */
    @Override
    public SysDictData selectDictDataById(Long dictCode) {
        return getById(dictCode);
    }

    /**
     * 批量删除字典数据信息
     * @param dictCodes 需要删除的字典数据ID
     */
    @Override
    public void deleteDictDataByIds(Long[] dictCodes) {
        for (Long dictCode : dictCodes) {
            SysDictData data = selectDictDataById(dictCode);
            removeById(dictCode);
            List<SysDictData> dictDataList = dictDataMapper.selectDictDataByType(data.getDictType());
            DictUtils.setDictCache(data.getDictType(), dictDataList);
        }
    }

    /**
     * 新增保存字典数据信息
     *
     * @param data 字典数据信息
     * @return 结果
     */
    @Override
    public int insertDictData(SysDictData data) {
        int row = dictDataMapper.insert(data);
        if (row > 0) {
            List<SysDictData> dictDataList = dictDataMapper.selectDictDataByType(data.getDictType());
            DictUtils.setDictCache(data.getDictType(), dictDataList);
        }
        return row;
    }

    /**
     * 修改保存字典数据信息
     * @param data 字典数据信息
     * @return 结果
     */
    @Override
    public int updateDictData(SysDictData data) {
        int row = dictDataMapper.updateById(data);
        if (row > 0) {
            List<SysDictData> dictDataList = dictDataMapper.selectDictDataByType(data.getDictType());
            DictUtils.setDictCache(data.getDictType(), dictDataList);
        }
        return row;
    }
}

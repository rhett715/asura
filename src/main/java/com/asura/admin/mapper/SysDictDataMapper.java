package com.asura.admin.mapper;

import com.asura.admin.entity.SysDictData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 字典数据表 Mapper 接口
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Mapper
public interface SysDictDataMapper extends BaseMapper<SysDictData> {

    default List<SysDictData> selectDictDataByType(String dictType) {
        return selectList(
                Wrappers.lambdaQuery(SysDictData.class)
                        .eq(SysDictData::getStatus, "0")
                        .eq(SysDictData::getDictType, dictType)
                        .orderByAsc(SysDictData::getDictSort));
    }
}

package com.asura.admin.mapper;

import com.asura.admin.entity.SysJobLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 定时任务调度日志表 Mapper 接口
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Mapper
public interface SysJobLogMapper extends BaseMapper<SysJobLog> {

}

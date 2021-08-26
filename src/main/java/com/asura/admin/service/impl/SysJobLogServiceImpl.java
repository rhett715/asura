package com.asura.admin.service.impl;

import com.asura.admin.entity.SysJobLog;
import com.asura.admin.mapper.SysJobLogMapper;
import com.asura.admin.service.SysJobLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 定时任务调度日志表 服务实现类
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Service
public class SysJobLogServiceImpl extends ServiceImpl<SysJobLogMapper, SysJobLog> implements SysJobLogService {

}

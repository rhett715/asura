package com.asura.admin.service.impl;

import com.asura.admin.entity.SysJob;
import com.asura.admin.mapper.SysJobMapper;
import com.asura.admin.service.SysJobService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 定时任务调度表 服务实现类
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Service
public class SysJobServiceImpl extends ServiceImpl<SysJobMapper, SysJob> implements SysJobService {

}

package com.asura.admin.controller.system;

import com.asura.admin.service.SysDictDataService;
import com.asura.admin.service.SysDictTypeService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 字典数据表 前端控制器
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Api(value = "数据字典信息控制器")
@RestController
@RequestMapping("/system/dict/data")
public class SysDictDataController {
    @Autowired
    private SysDictDataService dictDataService;
    @Autowired
    private SysDictTypeService dictTypeService;
}

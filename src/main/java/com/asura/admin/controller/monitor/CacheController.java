package com.asura.admin.controller.monitor;

import com.asura.admin.common.core.result.JsonResult;
import com.asura.admin.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @Author Rhett
 * @Date 2021/8/25
 * @Description
 */
@Api(value = "缓存监控")
@RestController
@RequestMapping("/monitor/cache")
public class CacheController {
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @ApiOperation(value = "获取缓存信息")
    @PreAuthorize("@ss.hasPermi('monitor:cache:list')")
    @GetMapping()
    public JsonResult<Map<String, Object>> getInfo() {
        Properties info = (Properties) redisTemplate.execute((RedisCallback<Object>) RedisServerCommands::info);
        Properties commandStats = (Properties) redisTemplate.execute((RedisCallback<Object>) connection -> connection.info("commandstats"));
        Object dbSize = redisTemplate.execute((RedisCallback<Object>) RedisServerCommands::dbSize);
        Map<String, Object> result = new HashMap<>(4);
        result.put("info", info);
        result.put("dbSize", dbSize);

        List<Map<String, String>> pieList = new ArrayList<>();
        commandStats.stringPropertyNames().forEach(key -> {
            Map<String, String> data = new HashMap<>(4);
            String property = commandStats.getProperty(key);
            data.put("name", StringUtil.removeStart(key, "cmdstat_"));
            data.put("value", StringUtil.substringBetween(property, "calls=", ",usec"));
            pieList.add(data);
        });
        result.put("commandStats", pieList);
        return JsonResult.success(result);
    }
}

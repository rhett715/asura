package com.asura.admin.controller.monitor;

import com.asura.admin.common.annotation.OptLog;
import com.asura.admin.common.constant.RedisConstants;
import com.asura.admin.common.core.page.TableDataInfo;
import com.asura.admin.common.core.result.JsonResult;
import com.asura.admin.common.enums.BusinessType;
import com.asura.admin.entity.SysUserOnline;
import com.asura.admin.security.LoginUser;
import com.asura.admin.service.SysUserOnlineService;
import com.asura.admin.util.PageUtils;
import com.asura.admin.util.RedisCache;
import com.asura.admin.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @Author Rhett
 * @Date 2021/8/25
 * @Description
 */
@Api(value = "在线用户监控控制器")
@RestController
@RequestMapping("/monitor/online")
public class SysUserOnlineController {
    @Autowired
    private SysUserOnlineService userOnlineService;
    @Autowired
    private RedisCache redisCache;

    @ApiOperation(value = "分页显示在线用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ipAddr", value = "IP地址", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String", paramType = "query")
    })
    @PreAuthorize("@ss.hasPermi('monitor:online:list')")
    @GetMapping("/list")
    public TableDataInfo<SysUserOnline> list(@RequestParam String ipAddr, @RequestParam String username) {
        Collection<String> keys = redisCache.keys(RedisConstants.LOGIN_TOKEN_KEY + "*");
        List<SysUserOnline> userOnlineList = new ArrayList<>();
        for (String key : keys) {
            LoginUser user = redisCache.getCacheObject(key);
            if (StringUtil.isNotEmpty(ipAddr) && StringUtil.isNotEmpty(username)) {
                if (StringUtil.equals(ipAddr, user.getIpAddr()) && StringUtil.equals(username, user.getUsername())) {
                    userOnlineList.add(userOnlineService.selectOnlineByInfo(ipAddr, username, user));
                }
            } else if (StringUtil.isNotEmpty(ipAddr)) {
                if (StringUtil.equals(ipAddr, user.getIpAddr())) {
                    userOnlineList.add(userOnlineService.selectOnlineByIpAddr(ipAddr, user));
                }
            } else if (StringUtil.isNotEmpty(username) && StringUtil.isNotNull(user.getUser())) {
                if (StringUtil.equals(username, user.getUsername())) {
                    userOnlineList.add(userOnlineService.selectOnlineByUserName(username, user));
                }
            } else {
                userOnlineList.add(userOnlineService.loginUserToUserOnline(user));
            }
        }
        Collections.reverse(userOnlineList);
        userOnlineList.removeAll(Collections.singleton(null));
        return PageUtils.buildDataInfo(userOnlineList);
    }

    @ApiOperation(value = "强退用户")
    @ApiImplicitParam(name = "tokenId", value = "唯一标识", required = true, dataType = "String", paramType = "path")
    @PreAuthorize("@ss.hasPermi('monitor:online:forceLogout')")
    @OptLog(title = "在线用户", businessType = BusinessType.FORCE)
    @DeleteMapping("/{tokenId}")
    public JsonResult<?> forceLogout(@PathVariable String tokenId) {
        redisCache.deleteObject(RedisConstants.LOGIN_TOKEN_KEY + tokenId);
        return JsonResult.success();
    }
}

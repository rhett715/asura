package com.asura.admin.controller.system;

import com.asura.admin.common.constant.SecurityConstants;
import com.asura.admin.common.core.result.JsonResult;
import com.asura.admin.domain.LoginBody;
import com.asura.admin.domain.RegisterBody;
import com.asura.admin.domain.vo.RouterVo;
import com.asura.admin.entity.SysMenu;
import com.asura.admin.entity.SysUser;
import com.asura.admin.security.LoginUser;
import com.asura.admin.security.TokenService;
import com.asura.admin.security.service.SysPermissionService;
import com.asura.admin.service.SysConfigService;
import com.asura.admin.service.SysMenuService;
import com.asura.admin.service.SysUserAuthService;
import com.asura.admin.util.SecurityUtils;
import com.asura.admin.util.ServletUtils;
import com.asura.admin.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author Rhett
 * @Date 2021/8/20
 * @Description
 */
@Api(value = "用户登录注册控制器")
@RestController
public class SysUserAuthController {
    @Autowired
    private SysUserAuthService userAuthService;
    @Autowired
    private SysMenuService menuService;
    @Autowired
    private SysPermissionService permissionService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private SysConfigService configService;

    @ApiOperation(value = "用户注册")
    @ApiImplicitParam(name = "registerBody", value = "用户注册对象", required = true, dataType = "RegisterBody", paramType = "body")
    @PostMapping("/register")
    public JsonResult<String> register(@RequestBody RegisterBody registerBody) {
        if (!("true".equals(configService.selectConfigByKey("sys.account.registerUser")))) {
            return JsonResult.fail("当前系统没有开启注册功能！");
        }
        String msg = userAuthService.register(registerBody);
        return StringUtil.isEmpty(msg) ? JsonResult.success() : JsonResult.fail(msg);
    }

    @ApiOperation(value = "用户登录")
    @ApiImplicitParam(name = "loginBody", value = "用户登录对象", required = true, dataType = "LoginBody", paramType = "body")
    @PostMapping("/login")
    public JsonResult<Map<String,Object>> login(@RequestBody LoginBody loginBody) {
        Map<String,Object> result = new HashMap<>();
        // 生成令牌
        String token = userAuthService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        result.put(SecurityConstants.TOKEN, token);
        return JsonResult.success(result);
    }

    @ApiOperation(value = "获取用户信息")
    @GetMapping("getInfo")
    public JsonResult<Map<String,Object>> getInfo() {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        SysUser user = loginUser.getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        Map<String,Object> result = new HashMap<>();
        result.put("user", user);
        result.put("roles", roles);
        result.put("permissions", permissions);
        return JsonResult.success(result);
    }

    @ApiOperation(value = "获取路由信息")
    @GetMapping("getRouters")
    public JsonResult<List<RouterVo>> getRouters() {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return JsonResult.success(menuService.buildMenus(menus));
    }
}

package com.asura.admin.service.impl;

import com.asura.admin.common.constant.Constants;
import com.asura.admin.common.constant.UserConstants;
import com.asura.admin.domain.TreeSelect;
import com.asura.admin.domain.vo.MetaVo;
import com.asura.admin.domain.vo.RouterVo;
import com.asura.admin.entity.SysMenu;
import com.asura.admin.entity.SysRole;
import com.asura.admin.entity.SysRoleMenu;
import com.asura.admin.entity.SysUser;
import com.asura.admin.mapper.SysMenuMapper;
import com.asura.admin.mapper.SysRoleMapper;
import com.asura.admin.mapper.SysRoleMenuMapper;
import com.asura.admin.service.SysMenuService;
import com.asura.admin.util.SecurityUtils;
import com.asura.admin.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单权限表 服务实现类
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {
    public static final String PERMISSION_STRING = "perms[\"{0}\"]";
    @Autowired
    private SysMenuMapper menuMapper;
    @Autowired
    private SysRoleMapper roleMapper;
    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    /**
     * 根据用户查询系统菜单列表
     * @param userId 用户ID
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuList(Long userId) {
        return selectMenuList(new SysMenu(), userId);
    }

    /**
     * 查询系统菜单列表
     * @param menu 菜单信息
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuList(SysMenu menu, Long userId) {
        List<SysMenu> menuList;
        // 管理员显示所有菜单信息
        if (SysUser.isAdmin(userId)) {
            menuList = list(Wrappers.lambdaQuery(SysMenu.class)
                    .like(StringUtil.isNotBlank(menu.getMenuName()),SysMenu::getMenuName,menu.getMenuName())
                    .eq(StringUtil.isNotBlank(menu.getVisible()),SysMenu::getVisible,menu.getVisible())
                    .eq(StringUtil.isNotBlank(menu.getStatus()),SysMenu::getStatus,menu.getStatus())
                    .orderByAsc(SysMenu::getParentId)
                    .orderByAsc(SysMenu::getOrderNum));
        } else {
            menu.getParams().put("userId", userId);
            menuList = menuMapper.selectMenuListByUserId(menu);
        }
        return menuList;
    }

    /**
     * 根据用户ID查询权限
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectMenuPermsByUserId(Long userId) {
        List<String> perms = menuMapper.selectMenuPermsByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (StringUtil.isNotEmpty(perm)) {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 根据用户ID查询菜单
     * @param userId 用户名称
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuTreeByUserId(Long userId) {
        List<SysMenu> menus;
        if (SecurityUtils.isAdmin(userId)) {
            menus = menuMapper.selectMenuTreeAll();
        } else {
            menus = menuMapper.selectMenuTreeByUserId(userId);
        }
        return getChildPerms(menus, 0);
    }

    /**
     * 根据角色ID查询菜单树信息
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    @Override
    public List<Long> selectMenuListByRoleId(Long roleId) {
        SysRole role = roleMapper.selectById(roleId);
        return menuMapper.selectMenuListByRoleId(roleId, role.getMenuCheckStrictly());
    }

    /**
     * 构建前端路由所需要的菜单
     * @param menus 菜单列表
     * @return 路由列表
     */
    @Override
    public List<RouterVo> buildMenus(List<SysMenu> menus) {
        List<RouterVo> routers = new LinkedList<>();
        for (SysMenu menu : menus) {
            RouterVo router = new RouterVo();
            router.setHidden("1".equals(menu.getVisible()));
            router.setName(getRouteName(menu));
            router.setPath(getRouterPath(menu));
            router.setComponent(getComponent(menu));
            router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), StringUtil.equals("1", menu.getIsCache()), menu.getPath()));
            List<SysMenu> cMenus = menu.getChildren();
            if (!cMenus.isEmpty() && UserConstants.TYPE_DIR.equals(menu.getMenuType())) {
                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                router.setChildren(buildMenus(cMenus));
            } else if (isMenuFrame(menu)) {
                router.setMeta(null);
                List<RouterVo> childrenList = new ArrayList<>();
                RouterVo children = new RouterVo();
                children.setPath(menu.getPath());
                children.setComponent(menu.getComponent());
                children.setName(StringUtil.capitalize(menu.getPath()));
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), StringUtil.equals("1", menu.getIsCache()), menu.getPath()));
                childrenList.add(children);
                router.setChildren(childrenList);
            } else if (menu.getParentId().intValue() == 0 && isInnerLink(menu)) {
                router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon()));
                router.setPath("/inner");
                List<RouterVo> childrenList = new ArrayList<>();
                RouterVo children = new RouterVo();
                String routerPath = StringUtil.replaceEach(menu.getPath(), new String[] { Constants.HTTP, Constants.HTTPS }, new String[] { "", "" });
                children.setPath(routerPath);
                children.setComponent(UserConstants.INNER_LINK);
                children.setName(StringUtil.capitalize(routerPath));
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), menu.getPath()));
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            routers.add(router);
        }
        return routers;
    }

    /**
     * 构建前端所需要树结构
     * @param menus 菜单列表
     * @return 树结构列表
     */
    @Override
    public List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        List<SysMenu> returnList = new ArrayList<>();
        List<Long> tempList = new ArrayList<>();
        for (SysMenu dept : menus) {
            tempList.add(dept.getMenuId());
        }
        for (SysMenu menu : menus) {
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(menu.getParentId())) {
                recursionFn(menus, menu);
                returnList.add(menu);
            }
        }
        if (returnList.isEmpty()) {
            returnList = menus;
        }
        return returnList;
    }

    /**
     * 构建前端所需要下拉树结构
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    @Override
    public List<TreeSelect> buildMenuTreeSelect(List<SysMenu> menus) {
        List<SysMenu> menuTrees = buildMenuTree(menus);
        return menuTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 根据菜单ID查询信息
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    @Override
    public SysMenu selectMenuById(Long menuId) {
        return getById(menuId);
    }

    /**
     * 是否存在菜单子节点
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public boolean hasChildByMenuId(Long menuId) {
        int result = count(Wrappers.lambdaQuery(SysMenu.class).eq(SysMenu::getParentId,menuId));
        return result > 0;
    }

    /**
     * 查询菜单使用数量
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public boolean checkMenuExistRole(Long menuId) {
        int result = roleMenuMapper.selectCount(Wrappers.lambdaQuery(SysRoleMenu.class).eq(SysRoleMenu::getMenuId, menuId));
        return result > 0;
    }

    /**
     * 新增保存菜单信息
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public int insertMenu(SysMenu menu) {
        return menuMapper.insert(menu);
    }

    /**
     * 修改保存菜单信息
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public int updateMenu(SysMenu menu) {
        return menuMapper.updateById(menu);
    }

    /**
     * 删除菜单管理信息
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public int deleteMenuById(Long menuId) {
        return menuMapper.deleteById(menuId);
    }

    /**
     * 校验菜单名称是否唯一
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public String checkMenuNameUnique(SysMenu menu) {
        Long menuId = StringUtil.isNull(menu.getMenuId()) ? -1L : menu.getMenuId();
        SysMenu info = getOne(Wrappers.lambdaQuery(SysMenu.class)
                .eq(SysMenu::getMenuName,menu.getMenuName())
                .eq(SysMenu::getParentId,menu.getParentId())
                .last("limit 1"));
        if (StringUtil.isNotNull(info) && info.getMenuId().longValue() != menuId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 获取路由名称
     * @param menu 菜单信息
     * @return 路由名称
     */
    public String getRouteName(SysMenu menu) {
        String routerName = StringUtil.capitalize(menu.getPath());
        // 非外链并且是一级目录（类型为目录）
        if (isMenuFrame(menu)) {
            routerName = StringUtil.EMPTY;
        }
        return routerName;
    }

    /**
     * 获取路由地址
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(SysMenu menu) {
        String routerPath = menu.getPath();
        // 内链打开外网方式
        if (menu.getParentId().intValue() != 0 && isInnerLink(menu)) {
            routerPath = StringUtil.replaceEach(routerPath, new String[] { Constants.HTTP, Constants.HTTPS }, new String[] { "", "" });
        }
        // 非外链并且是一级目录（类型为目录）
        if (0 == menu.getParentId().intValue() && UserConstants.TYPE_DIR.equals(menu.getMenuType())
                && UserConstants.NO_FRAME.equals(menu.getIsFrame())) {
            routerPath = "/" + menu.getPath();
        }
        // 非外链并且是一级目录（类型为菜单）
        else if (isMenuFrame(menu)) {
            routerPath = "/";
        }
        return routerPath;
    }

    /**
     * 获取组件信息
     * @param menu 菜单信息
     * @return 组件信息
     */
    public String getComponent(SysMenu menu) {
        String component = UserConstants.LAYOUT;
        if (StringUtil.isNotEmpty(menu.getComponent()) && !isMenuFrame(menu)) {
            component = menu.getComponent();
        } else if (StringUtil.isEmpty(menu.getComponent()) && menu.getParentId().intValue() != 0 && isInnerLink(menu)) {
            component = UserConstants.INNER_LINK;
        } else if (StringUtil.isEmpty(menu.getComponent()) && isParentView(menu)) {
            component = UserConstants.PARENT_VIEW;
        }
        return component;
    }

    /**
     * 是否为菜单内部跳转
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isMenuFrame(SysMenu menu) {
        return menu.getParentId().intValue() == 0 && UserConstants.TYPE_MENU.equals(menu.getMenuType())
                && menu.getIsFrame().equals(UserConstants.NO_FRAME);
    }

    /**
     * 是否为内链组件
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isInnerLink(SysMenu menu) {
        return menu.getIsFrame().equals(UserConstants.NO_FRAME) && StringUtil.isHttp(menu.getPath());
    }

    /**
     * 是否为parent_view组件
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isParentView(SysMenu menu) {
        return menu.getParentId().intValue() != 0 && UserConstants.TYPE_DIR.equals(menu.getMenuType());
    }

    /**
     * 根据父节点的ID获取所有子节点
     * @param list 菜单信息列表
     * @param parentId 传入的父节点ID
     * @return String
     */
    public List<SysMenu> getChildPerms(List<SysMenu> list, int parentId) {
        List<SysMenu> returnList = new ArrayList<>();
        for (SysMenu t : list) {
            // 一、根据传入的某个父节点ID,遍历该父节点的所有子节点
            if (t.getParentId() == parentId) {
                recursionFn(list, t);
                returnList.add(t);
            }
        }
        return returnList;
    }

    /**
     * 递归列表
     * @param list 菜单信息列表
     * @param t 菜单信息
     */
    private void recursionFn(List<SysMenu> list, SysMenu t) {
        // 得到子节点列表
        List<SysMenu> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysMenu tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysMenu> getChildList(List<SysMenu> list, SysMenu t) {
        List<SysMenu> tList = new ArrayList<>();
        for (SysMenu n : list) {
            if (n.getParentId().longValue() == t.getMenuId().longValue()) {
                tList.add(n);
            }
        }
        return tList;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysMenu> list, SysMenu t) {
        return getChildList(list, t).size() > 0;
    }
}

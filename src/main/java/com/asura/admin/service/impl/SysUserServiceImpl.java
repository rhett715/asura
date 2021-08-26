package com.asura.admin.service.impl;

import com.asura.admin.common.constant.UserConstants;
import com.asura.admin.common.core.page.TableDataInfo;
import com.asura.admin.entity.*;
import com.asura.admin.exception.BusinessException;
import com.asura.admin.mapper.*;
import com.asura.admin.service.SysConfigService;
import com.asura.admin.service.SysUserService;
import com.asura.admin.util.PageUtils;
import com.asura.admin.util.SecurityUtils;
import com.asura.admin.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private SysRoleMapper roleMapper;
    @Autowired
    private SysPostMapper postMapper;
    @Autowired
    private SysUserRoleMapper userRoleMapper;
    @Autowired
    private SysUserPostMapper userPostMapper;
    @Autowired
    private SysConfigService configService;

    /**
     * 根据条件分页查询用户列表
     * @param user 用户信息
     * @return 用户信息集合
     */
    @Override
    public TableDataInfo<SysUser> selectPageUserList(SysUser user) {
        return PageUtils.buildDataInfo(userMapper.selectPageUserList(PageUtils.buildPage(), user));
    }

    /**
     * 根据条件查询用户列表
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public List<SysUser> selectUserList(SysUser user) {
        return userMapper.selectUserList(user);
    }

    /**
     * 根据条件分页查询已分配用户角色列表
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public TableDataInfo<SysUser> selectAllocatedList(SysUser user) {
        return PageUtils.buildDataInfo(userMapper.selectAllocatedList(PageUtils.buildPage(), user));
    }

    /**
     * 根据条件分页查询未分配用户角色列表
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public TableDataInfo<SysUser> selectUnallocatedList(SysUser user) {
        return PageUtils.buildDataInfo(userMapper.selectUnallocatedList(PageUtils.buildPage(), user));
    }

    /**
     * 通过用户名查询用户
     * @param userName 用户名
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserByUserName(String userName) {
        return userMapper.selectUserByUserName(userName);
    }

    /**
     * 通过用户ID查询用户
     * @param userId 用户ID
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserById(Long userId) {
        return userMapper.selectUserById(userId);
    }

    /**
     * 查询用户所属角色组
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserRoleGroup(String userName) {
        List<SysRole> list = roleMapper.selectRolesByUserName(userName);
        StringBuilder idsStr = new StringBuilder();
        for (SysRole role : list) {
            idsStr.append(role.getRoleName()).append(",");
        }
        if (StringUtil.isNotEmpty(idsStr.toString())) {
            return idsStr.substring(0, idsStr.length() - 1);
        }
        return idsStr.toString();
    }

    /**
     * 查询用户所属岗位组
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserPostGroup(String userName) {
        List<SysPost> list = postMapper.selectPostsByUserName(userName);
        StringBuilder idsStr = new StringBuilder();
        for (SysPost post : list) {
            idsStr.append(post.getPostName()).append(",");
        }
        if (StringUtil.isNotEmpty(idsStr.toString())) {
            return idsStr.substring(0, idsStr.length() - 1);
        }
        return idsStr.toString();
    }

    /**
     * 校验用户名称是否唯一
     * @param userName 用户名称
     * @return 结果
     */
    @Override
    public String checkUserNameUnique(String userName) {
        int count = count(Wrappers.lambdaQuery(SysUser.class).eq(SysUser::getUserName, userName).last("limit 1"));
        if (count > 0) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验用户名称是否唯一
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public String checkPhoneUnique(SysUser user) {
        Long userId = StringUtil.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.selectOne(Wrappers.lambdaQuery(SysUser.class)
                .select(SysUser::getUserId, SysUser::getPhoneNumber)
                .eq(SysUser::getPhoneNumber, user.getPhoneNumber()).last("limit 1"));
        if (StringUtil.isNotNull(info) && info.getUserId().longValue() != userId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验email是否唯一
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public String checkEmailUnique(SysUser user) {
        Long userId = StringUtil.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.selectOne(Wrappers.lambdaQuery(SysUser.class)
                .select(SysUser::getUserId, SysUser::getEmail)
                .eq(SysUser::getEmail, user.getEmail()).last("limit 1"));
        if (StringUtil.isNotNull(info) && info.getUserId().longValue() != userId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验用户是否允许操作
     * @param user 用户信息
     */
    @Override
    public void checkUserAllowed(SysUser user) {
        if (StringUtil.isNotNull(user.getUserId()) && user.isAdmin()) {
            throw new BusinessException("不允许操作超级管理员用户");
        }
    }

    /**
     * 新增保存用户信息
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional
    public int insertUser(SysUser user) {
        // 新增用户信息
        int rows = userMapper.insert(user);
        // 新增用户岗位关联
        insertUserPost(user);
        // 新增用户与角色管理
        insertUserRole(user);
        return rows;
    }

    /**
     * 注册用户信息
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public boolean registerUser(SysUser user) {
        return userMapper.insert(user) > 0;
    }

    /**
     * 修改保存用户信息
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateUser(SysUser user) {
        Long userId = user.getUserId();
        // 删除用户与角色关联
        userRoleMapper.delete(Wrappers.lambdaQuery(SysUserRole.class).eq(SysUserRole::getUserId,userId));
        // 新增用户与角色管理
        insertUserRole(user);
        // 删除用户与岗位关联
        userPostMapper.delete(Wrappers.lambdaQuery(SysUserPost.class).eq(SysUserPost::getUserId,userId));
        // 新增用户与岗位管理
        insertUserPost(user);
        return userMapper.updateById(user);
    }

    /**
     * 用户授权角色
     * @param userId 用户ID
     * @param roleIds 角色组
     */
    @Override
    @Transactional
    public void insertUserAuth(Long userId, Long[] roleIds) {
        userRoleMapper.delete(Wrappers.lambdaQuery(SysUserRole.class)
                .eq(SysUserRole::getUserId, userId));
        insertUserRole(userId, roleIds);
    }

    /**
     * 修改用户状态
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserStatus(SysUser user) {
        return userMapper.updateById(user);
    }

    /**
     * 修改用户基本信息
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserProfile(SysUser user) {
        return userMapper.updateById(user);
    }

    /**
     * 修改用户头像
     * @param userName 用户名
     * @param avatar 头像地址
     * @return 结果
     */
    @Override
    public boolean updateUserAvatar(String userName, String avatar) {
        return userMapper.update(null,
                Wrappers.lambdaUpdate(SysUser.class)
                        .set(SysUser::getAvatar,avatar)
                        .eq(SysUser::getUserName,userName)) > 0;
    }

    /**
     * 重置用户密码
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int resetPwd(SysUser user) {
        return userMapper.updateById(user);
    }

    /**
     * 重置用户密码
     * @param userName 用户名
     * @param password 密码
     * @return 结果
     */
    @Override
    public int resetUserPwd(String userName, String password) {
        return userMapper.update(null,
                Wrappers.lambdaUpdate(SysUser.class)
                        .set(SysUser::getPassword,password)
                        .eq(SysUser::getUserName,userName));
    }

    /**
     * 新增用户角色信息
     * @param user 用户对象
     */
    public void insertUserRole(SysUser user) {
        Long[] roles = user.getRoleIds();
        if (StringUtil.isNotNull(roles)) {
            // 新增用户与角色管理
            List<SysUserRole> userRoleList = new ArrayList<>();
            for (Long roleId : roles) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(user.getUserId());
                ur.setRoleId(roleId);
                userRoleList.add(ur);
            }
            if (userRoleList.size() > 0) {
                userRoleMapper.insertBatchUserRole(userRoleList);
            }
        }
    }

    /**
     * 新增用户岗位信息
     * @param user 用户对象
     */
    public void insertUserPost(SysUser user) {
        Long[] posts = user.getPostIds();
        if (StringUtil.isNotNull(posts)) {
            // 新增用户与岗位管理
            List<SysUserPost> userPostList = new ArrayList<>();
            for (Long postId : posts) {
                SysUserPost up = new SysUserPost();
                up.setUserId(user.getUserId());
                up.setPostId(postId);
                userPostList.add(up);
            }
            if (userPostList.size() > 0) {
                userPostMapper.insertBatchUserPost(userPostList);
            }
        }
    }

    /**
     * 新增用户角色信息
     * @param userId 用户ID
     * @param roleIds 角色组
     */
    public void insertUserRole(Long userId, Long[] roleIds) {
        if (StringUtil.isNotNull(roleIds)) {
            // 新增用户与角色管理
            List<SysUserRole> userRoleList = new ArrayList<>();
            for (Long roleId : roleIds) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                userRoleList.add(ur);
            }
            if (userRoleList.size() > 0) {
                userRoleMapper.insertBatchUserRole(userRoleList);
            }
        }
    }

    /**
     * 通过用户ID删除用户
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteUserById(Long userId) {
        // 删除用户与角色关联
        userRoleMapper.delete(Wrappers.lambdaQuery(SysUserRole.class).eq(SysUserRole::getUserId,userId));
        // 删除用户与岗位表
        userPostMapper.delete(Wrappers.lambdaQuery(SysUserPost.class).eq(SysUserPost::getUserId,userId));
        return userMapper.deleteById(userId);
    }

    /**
     * 批量删除用户信息
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteUserByIds(Long[] userIds) {
        for (Long userId : userIds) {
            checkUserAllowed(new SysUser(userId));
        }
        List<Long> ids = Arrays.asList(userIds);
        // 删除用户与角色关联
        userRoleMapper.delete(Wrappers.lambdaQuery(SysUserRole.class).in(SysUserRole::getUserId,ids));
        // 删除用户与岗位表
        userPostMapper.delete(Wrappers.lambdaQuery(SysUserPost.class).in(SysUserPost::getUserId,ids));
        return userMapper.deleteBatchIds(ids);
    }

    /**
     * 导入用户数据
     * @param userList 用户数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param optName 操作用户
     * @return 结果
     */
    @Override
    public String importUser(List<SysUser> userList, Boolean isUpdateSupport, String optName) {
        if (StringUtil.isNull(userList) || userList.size() == 0) {
            throw new BusinessException("导入用户数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        String password = configService.selectConfigByKey("sys.user.initPassword");
        for (SysUser user : userList) {
            try {
                // 验证是否存在这个用户
                SysUser u = userMapper.selectUserByUserName(user.getUserName());
                if (StringUtil.isNull(u)) {
                    user.setPassword(SecurityUtils.encryptPassword(password));
                    user.setCreateBy(optName);
                    this.insertUser(user);
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、账号 ").append(user.getUserName()).append(" 导入成功");
                } else if (isUpdateSupport) {
                    user.setUpdateBy(optName);
                    this.updateUser(user);
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、账号 ").append(user.getUserName()).append(" 更新成功");
                } else {
                    failureNum++;
                    failureMsg.append("<br/>").append(failureNum).append("、账号 ").append(user.getUserName()).append(" 已存在");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、账号 " + user.getUserName() + " 导入失败：";
                failureMsg.append(msg).append(e.getMessage());
                log.error(msg, e);
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new BusinessException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }
}

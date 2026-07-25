package com.example.sea.system.service.impl;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Objects;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.sea.common.mybatis.constants.DeletedEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.core.result.PageResult;
import com.example.sea.common.security.entity.LoginUser;
import com.example.sea.common.security.utils.SecurityContextUtil;
import com.example.sea.system.api.constants.PermissionConstants;
import com.example.sea.system.converter.SysUserConverter;
import com.example.sea.system.dao.SysUserMapper;
import com.example.sea.system.entity.SysUserPO;
import com.example.sea.system.api.dto.SysUserDTO;
import com.example.sea.system.api.param.SysUserQueryParam;
import com.example.sea.system.api.vo.SysUserVO;
import com.example.sea.system.service.ISysUserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户表服务实现类
 * @author liuhuan
 * @date 2025-05-28
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUserPO> implements ISysUserService {

    private final SysUserConverter sysUserConverter;

    private final BCryptPasswordEncoder bCryptPasswordEncoder =  new BCryptPasswordEncoder();

    /**
     * 新增用户信息
     */
    @Override
    public CommonResult<Boolean> add(SysUserDTO sysUserDTO) {
        try {
            SysUserPO entity = sysUserConverter.dtoToEntity(sysUserDTO);
            //BCrypt加密密码
            entity.setPasswordHash(bCryptPasswordEncoder.encode(sysUserDTO.getPassword()));
            boolean result = this.save(entity);
            return CommonResult.success(result);
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause instanceof SQLIntegrityConstraintViolationException) {
                log.error("用户新增失败，用户名已存在：{}", cause.getMessage());
                return CommonResult.failed("该用户已存在");
            }
            log.error("用户新增失败：{}", e.getMessage(), e);
            return CommonResult.failed("用户新增失败");
        }
    }

    /**
     * 更新用户信息
     */
    @Override
    public CommonResult<Boolean> update(SysUserDTO sysUserDTO) {
        SysUserPO entity = sysUserConverter.dtoToEntity(sysUserDTO);
        //BCrypt加密密码
        if (Objects.nonNull(sysUserDTO.getPassword())) {
            entity.setPasswordHash(bCryptPasswordEncoder.encode(sysUserDTO.getPassword()));
        }
        boolean result = this.updateById(entity);
        return CommonResult.success(result);
    }

    @Override
    public CommonResult<List<SysUserVO>> list(SysUserQueryParam sysUserQueryParam) {
        LambdaQueryWrapper<SysUserPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserPO::getDelFlag, DeletedEnum.NORMAL.getCode())
                .likeRight(StringUtils.isNotBlank(sysUserQueryParam.getUsername()), SysUserPO::getUsername, sysUserQueryParam.getUsername())
                .likeRight(StringUtils.isNotBlank(sysUserQueryParam.getEmail()), SysUserPO::getEmail, sysUserQueryParam.getEmail())
                .eq(Objects.nonNull(sysUserQueryParam.getMobile()), SysUserPO::getMobile, sysUserQueryParam.getMobile())
                .likeRight(StringUtils.isNotBlank(sysUserQueryParam.getAvatarUrl()), SysUserPO::getAvatarUrl, sysUserQueryParam.getAvatarUrl())
                .gt(Objects.nonNull(sysUserQueryParam.getCreateTimeStart()), SysUserPO::getCreateTime, sysUserQueryParam.getCreateTimeStart())
                .le(Objects.nonNull(sysUserQueryParam.getCreateTimeEnd()), SysUserPO::getCreateTime, sysUserQueryParam.getCreateTimeEnd());

        List<SysUserPO> userList = this.baseMapper.selectList(wrapper);
        return CommonResult.success(sysUserConverter.convertPoListToVoList(userList));
    }

    @Override
    public CommonResult<PageResult<SysUserVO>> page(SysUserQueryParam sysUserQueryParam) {
        IPage<SysUserPO> page = new Page<>(sysUserQueryParam.getPageNum(), sysUserQueryParam.getPageSize());
        LambdaQueryWrapper<SysUserPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserPO::getDelFlag, DeletedEnum.NORMAL.getCode())
                .likeRight(StringUtils.isNotBlank(sysUserQueryParam.getUsername()), SysUserPO::getUsername, sysUserQueryParam.getUsername())
                .likeRight(StringUtils.isNotBlank(sysUserQueryParam.getEmail()), SysUserPO::getEmail, sysUserQueryParam.getEmail())
                .eq(Objects.nonNull(sysUserQueryParam.getMobile()), SysUserPO::getMobile, sysUserQueryParam.getMobile())
                .likeRight(StringUtils.isNotBlank(sysUserQueryParam.getAvatarUrl()), SysUserPO::getAvatarUrl, sysUserQueryParam.getAvatarUrl())
                .gt(Objects.nonNull(sysUserQueryParam.getCreateTimeStart()), SysUserPO::getCreateTime, sysUserQueryParam.getCreateTimeStart())
                .le(Objects.nonNull(sysUserQueryParam.getCreateTimeEnd()), SysUserPO::getCreateTime, sysUserQueryParam.getCreateTimeEnd());
        this.baseMapper.selectPage(page, wrapper);

        //构建返回值
        PageResult<SysUserVO> pageResult = new PageResult<>();
        pageResult.setRows(sysUserConverter.convertPoListToVoList(page.getRecords()));
        pageResult.setTotal(page.getTotal());
        pageResult.setPageNum(page.getPages());
        pageResult.setPageSize(page.getSize());
        return CommonResult.success(pageResult);
    }
    
    /**
     * 加载用户信息（PO + 角色 + 权限），返回完整 LoginUser（含 password hash）。
     * 内部使用，由 {@link #getLoginUser} 和 {@link #getAuthLoginUser} 复用。
     */
    private LoginUser loadLoginUserInternal(String username) {
        List<SysUserPO> userList = this.baseMapper.selectList(
                Wrappers.<SysUserPO>lambdaQuery().eq(SysUserPO::getUsername, username)
        );
        if (CollectionUtils.isEmpty(userList)) {
            return null;
        }
        SysUserPO sysUser = userList.get(0);
        LoginUser loginUser = new LoginUser();
        BeanUtils.copyProperties(sysUser, loginUser);
        loginUser.setPassword(sysUser.getPasswordHash());
        loginUser.setRoles(this.baseMapper.getRoleCodeByUserId(sysUser.getId()));
        loginUser.setPerms(this.baseMapper.getPermsByUserId(sysUser.getId()));
        return loginUser;
    }

    /**
     * UI 视图：剔除 password 字段，避免 BCrypt 哈希泄漏给前端。
     */
    @Override
    public CommonResult<com.example.sea.system.api.dto.LoginUserView> getLoginUser(String username) {
        if (StringUtils.isBlank(username)) {
            return CommonResult.failed("用户名不能为空");
        }
        LoginUser loginUser = loadLoginUserInternal(username);
        if (loginUser == null) {
            return CommonResult.failed("用户不存在");
        }
        return CommonResult.success(com.example.sea.system.api.dto.LoginUserView.from(loginUser));
    }

    /**
     * 内部鉴权视图：含 password hash，仅供 sea-auth Feign 走 {@code internal:callback} 调用。
     */
    @Override
    public CommonResult<LoginUser> getAuthLoginUser(String username) {
        if (StringUtils.isBlank(username)) {
            return CommonResult.failed("用户名不能为空");
        }
        LoginUser loginUser = loadLoginUserInternal(username);
        if (loginUser == null) {
            return CommonResult.failed("用户不存在");
        }
        return CommonResult.success(loginUser);
    }

    /**
     * 删除用户
     */
    @Override
    public CommonResult<Boolean> delete(Long userId) {
        boolean result = this.removeById(userId);
        return CommonResult.success(result);
    }

    @Override
    public CommonResult<Boolean> resetPassword(Long userId, String newPassword, Boolean requireChange) {
        if (userId == null || newPassword == null || newPassword.isBlank()) {
            return CommonResult.failed("参数缺失");
        }
        SysUserPO user = this.getById(userId);
        if (user == null) return CommonResult.failed("用户不存在");
        user.setPasswordHash(bCryptPasswordEncoder.encode(newPassword));
        user.setRequirePasswordChange(Boolean.TRUE.equals(requireChange) ? 1 : 0);
        boolean ok = this.updateById(user);
        return ok ? CommonResult.success(true) : CommonResult.failed("更新失败");
    }

    @Override
    public CommonResult<java.util.Map<String, Object>> getUserRaw(Long userId) {
        SysUserPO user = this.getById(userId);
        if (user == null) return CommonResult.failed("用户不存在");
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("email", user.getEmail());
        data.put("mobile", user.getMobile());
        data.put("deptId", user.getDeptId());
        data.put("leaderId", user.getLeaderId());
        data.put("level", user.getLevel());
        data.put("requirePasswordChange", user.getRequirePasswordChange());
        data.put("status", user.getStatus());
        return CommonResult.success(data);
    }

    @Override
    public CommonResult<Long> getUserLeaderId(Long userId) {
        SysUserPO user = this.getById(userId);
        if (user == null) return CommonResult.failed("用户不存在");
        return CommonResult.success(user.getLeaderId());
    }

    @Override
    public CommonResult<Boolean> changePassword(Long userId, String oldPassword, String newPassword) {
        if (userId == null || newPassword == null || newPassword.isBlank()) {
            return CommonResult.failed("参数缺失");
        }
        // 二次校验：仅本人可改本人；admin（拥有 sys:user:edit）可代改。
        // 防止 controller 注解被绕过（AOP 关闭、注解漏写等）。
        Long callerId = SecurityContextUtil.getUserId();
        boolean isSelf = callerId != null && callerId.equals(userId);
        boolean isAdmin = SecurityContextUtil.hasAuthority(PermissionConstants.SYS_USER_EDIT);
        if (!isSelf && !isAdmin) {
            return CommonResult.failed("无权修改他人密码");
        }

        SysUserPO user = this.getById(userId);
        if (user == null) return CommonResult.failed("用户不存在");

        // 仅当"强制改密"场景下允许 oldPassword 为空（sea-auth 已通过密码登录）。
        // 其他场景必须校验 oldPassword，禁止靠"省略参数"绕过 BCrypt。
        boolean forcedReset = Integer.valueOf(1).equals(user.getRequirePasswordChange());
        if (forcedReset) {
            // 强制改密流程：sea-auth 已用临时密码登录过，此处不重复校验 oldPassword
        } else {
            if (oldPassword == null || oldPassword.isBlank()) {
                return CommonResult.failed("原密码不能为空");
            }
            if (!bCryptPasswordEncoder.matches(oldPassword, user.getPasswordHash())) {
                return CommonResult.failed("原密码不正确");
            }
        }
        user.setPasswordHash(bCryptPasswordEncoder.encode(newPassword));
        user.setRequirePasswordChange(0);
        boolean ok = this.updateById(user);
        return ok ? CommonResult.success(true) : CommonResult.failed("更新失败");
    }

}

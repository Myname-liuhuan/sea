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
     * 校验登录用户信息
     * @param username 用户名
     * @return 返回登录用户信息
     */
    @Override
    public CommonResult<LoginUser> getLoginUser(String username) {
        if (StringUtils.isBlank(username)) {
            return CommonResult.failed("用户名不能为空");
        }
        //lambdaquery通过用户名查询用户
        List<SysUserPO> userList = this.baseMapper.selectList(
            Wrappers.<SysUserPO>lambdaQuery().eq(SysUserPO::getUsername, username)
        );

        if (CollectionUtils.isEmpty(userList)) {
            return CommonResult.failed("用户不存在");
        }

        SysUserPO sysUser = userList.get(0);
        LoginUser loginUser = new LoginUser();
        BeanUtils.copyProperties(sysUser, loginUser);
        loginUser.setPassword(sysUser.getPasswordHash());
        //获取角色
        List<String> roleCodeList = this.baseMapper.getRoleCodeByUserId(sysUser.getId());
        loginUser.setRoles(roleCodeList);
        //获取权限
        List<String> perms = this.baseMapper.getPermsByUserId(sysUser.getId());
        loginUser.setPerms(perms);
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
        SysUserPO user = this.getById(userId);
        if (user == null) return CommonResult.failed("用户不存在");

        if (oldPassword != null && !oldPassword.isBlank()) {
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

package com.example.sea.system.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.core.result.PageResult;
import com.example.sea.common.security.entity.LoginUser;
import com.example.sea.system.entity.SysUserPO;
import com.example.sea.system.api.dto.SysUserDTO;
import com.example.sea.system.api.param.SysUserQueryParam;
import com.example.sea.system.api.vo.SysUserVO;

/**
 * 用户表服务接口
 * @author liuhuan
 * @date 2025-05-28
 */
public interface ISysUserService extends IService<SysUserPO> {

    /**
     * 保存用户信息
     * @param sysUserDTO 入参
     * @return
     */
    CommonResult<Boolean> add(SysUserDTO sysUserDTO);

    /**
     * 更新用户信息
     * @param sysUserDTO 入参
     * @return 结果
     */
    CommonResult<Boolean> update(SysUserDTO sysUserDTO);

    /**
     * 查询用户列表
     * @param sysUserQueryParam 查询参数
     * @return 用户列表
     */
    CommonResult<List<SysUserVO>> list(SysUserQueryParam sysUserQueryParam);

    /**
     * 分页查询用户列表
     * @param sysUserQueryParam 查询参数
     * @return 分页结果
     */
    CommonResult<PageResult<SysUserVO>> page(SysUserQueryParam sysUserQueryParam);

    /**
     * 取登录用户视图（不含 password），给 UI / sysUser/getLoginUser 用。
     * 避免把 BCrypt 哈希泄漏给前端或非内部调用方。
     */
    CommonResult<com.example.sea.system.api.dto.LoginUserView> getLoginUser(String username);

    /**
     * 取完整登录用户（含 password hash），仅供 sea-auth 走 Feign 内部端点调用。
     * 调用方需持有 {@code internal:callback} 权限。
     */
    CommonResult<LoginUser> getAuthLoginUser(String username);

    /**
     * 删除用户
     * @param userId 用户ID
     * @return 结果
     */
    CommonResult<Boolean> delete(Long userId);


    /**
     * 重置密码（流程回调用，不走菜单权限）。
     *
     * <p>用 Bcrypt 重写 password_hash，并将 require_password_change 置 1，
     * 强制用户首次登录修改。
     */
    CommonResult<Boolean> resetPassword(Long userId, String newPassword, Boolean requireChange);

    /**
     * 取用户字段（不含密码），给流程调用方使用。
     * 仅做内部服务间调用；不做菜单权限控制。
     */
    CommonResult<java.util.Map<String, Object>> getUserRaw(Long userId);

    /**
     * 取直属上级 user_id，无上级返 null。
     */
    CommonResult<Long> getUserLeaderId(Long userId);

    /**
     * 自助改密：oldPassword 校验后写 newPassword 并清 require_password_change。
     * 调用方（sea-auth / sea-frontend）传 userId 与 newPassword 必填，oldPassword 可空。
     */
    CommonResult<Boolean> changePassword(Long userId, String oldPassword, String newPassword);

}

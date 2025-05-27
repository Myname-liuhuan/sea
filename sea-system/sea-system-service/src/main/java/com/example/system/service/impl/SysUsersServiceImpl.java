package com.example.system.service.impl;

import com.example.system.service.ISysUsersService;
import com.example.system.dao.SysUsersMapper;
import com.example.system.entity.SysUsers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 用户表服务实现类
 */
@Service
public class SysUsersServiceImpl extends ServiceImpl<SysUsersMapper, SysUsers> implements ISysUsersService {

}

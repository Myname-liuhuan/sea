package com.example.sea.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.core.result.PageResult;
import com.example.sea.notification.api.vo.InAppMessageVO;
import com.example.sea.notification.converter.InAppMessageConverter;
import com.example.sea.notification.dao.InAppMessageMapper;
import com.example.sea.notification.entity.InAppMessagePO;
import com.example.sea.notification.service.IInAppMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * 站内信服务。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Service
@RequiredArgsConstructor
public class InAppMessageServiceImpl implements IInAppMessageService {

    private final InAppMessageMapper mapper;
    private final InAppMessageConverter converter;

    @Override
    public CommonResult<Long> unreadCount(Long userId) {
        Long count = mapper.selectCount(Wrappers.<InAppMessagePO>lambdaQuery()
                .eq(InAppMessagePO::getUserId, userId)
                .eq(InAppMessagePO::getReadFlag, 0));
        return CommonResult.success(count);
    }

    @Override
    public CommonResult<PageResult<InAppMessageVO>> myInbox(Long userId, Long pageNum, Long pageSize) {
        Page<InAppMessagePO> page = new Page<>(
                pageNum == null ? 1 : pageNum,
                pageSize == null ? 10 : pageSize);
        LambdaQueryWrapper<InAppMessagePO> w = Wrappers.<InAppMessagePO>lambdaQuery()
                .eq(InAppMessagePO::getUserId, userId)
                .orderByDesc(InAppMessagePO::getCreatedAt);
        IPage<InAppMessagePO> result = mapper.selectPage(page, w);
        return CommonResult.success(new PageResult<>(
                converter.entityListToVoList(result.getRecords()),
                result.getTotal(),
                result.getCurrent(),
                result.getSize()));
    }

    @Override
    public CommonResult<Void> markRead(Long userId, Long messageId) {
        LambdaUpdateWrapper<InAppMessagePO> w = Wrappers.<InAppMessagePO>lambdaUpdate()
                .eq(InAppMessagePO::getId, messageId)
                .eq(InAppMessagePO::getUserId, userId)
                .set(InAppMessagePO::getReadFlag, 1);
        mapper.update(null, w);
        return CommonResult.success();
    }

    @Override
    public CommonResult<Void> markAllRead(Long userId) {
        LambdaUpdateWrapper<InAppMessagePO> w = Wrappers.<InAppMessagePO>lambdaUpdate()
                .eq(InAppMessagePO::getUserId, userId)
                .eq(InAppMessagePO::getReadFlag, 0)
                .set(InAppMessagePO::getReadFlag, 1);
        mapper.update(null, w);
        return CommonResult.success();
    }
}

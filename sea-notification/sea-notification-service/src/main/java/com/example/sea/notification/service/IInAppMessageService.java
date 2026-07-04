package com.example.sea.notification.service;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.core.result.PageResult;
import com.example.sea.notification.api.vo.InAppMessageVO;

/**
 * 站内信。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
public interface IInAppMessageService {

    /** 当前登录人未读数 */
    CommonResult<Long> unreadCount(Long userId);

    /** 当前登录人收件箱（按时间倒序，分页） */
    CommonResult<PageResult<InAppMessageVO>> myInbox(Long userId, Long pageNum, Long pageSize);

    /** 标记一条已读 */
    CommonResult<Void> markRead(Long userId, Long messageId);

    /** 标记全部已读 */
    CommonResult<Void> markAllRead(Long userId);
}

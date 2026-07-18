package com.example.sea.workflow.api.feign.fallback;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.notification.api.dto.NotifyDTO;
import com.example.sea.notification.api.vo.NotifyVO;
import com.example.sea.workflow.api.feign.NotifyFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * sea-notification Feign 降级工厂。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Slf4j
@Component
public class NotifyFeignClientFallBack implements FallbackFactory<NotifyFeignClient> {

    @Override
    public NotifyFeignClient create(Throwable cause) {
        return new NotifyFeignClient() {
            @Override
            public CommonResult<NotifyVO> send(NotifyDTO request) {
                log.error("notify.send failed bizKey={} cause={}",
                        request == null ? null : request.getBizKey(), cause.getMessage());
                return CommonResult.failed("sea-notification 不可用: " + cause.getMessage());
            }

            @Override
            public CommonResult<Long> unreadCount(Long userId) {
                return CommonResult.success(0L);
            }
        };
    }
}
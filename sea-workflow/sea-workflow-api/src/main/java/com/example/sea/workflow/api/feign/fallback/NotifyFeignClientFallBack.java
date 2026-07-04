package com.example.sea.workflow.api.feign.fallback;

import com.example.sea.workflow.api.feign.NotifyFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            public Map<String, Object> send(Map<String, Object> payload) {
                log.error("notify.send failed cause={}", cause.getMessage());
                Map<String, Object> resp = new HashMap<>();
                resp.put("success", false);
                resp.put("error", "sea-notification 不可用: " + cause.getMessage());
                return resp;
            }

            @Override
            public Map<String, Object> sendInApp(Map<String, Object> payload) {
                return send(payload);
            }

            @Override
            public Long unreadCount(List<Long> userIds) {
                return 0L;
            }
        };
    }
}

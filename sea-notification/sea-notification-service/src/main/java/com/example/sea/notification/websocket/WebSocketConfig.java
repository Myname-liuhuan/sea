package com.example.sea.notification.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket 配置。
 *
 * <p>唯一入口 {@code /api/notification/ws/notify}：强制走网关（前端连
 * {@code ws://gateway-host:8080/api/notification/ws/notify?token=<JWT>}）。
 *
 * <p>不允许直连 notification-service 自身端口（包括 {@code /ws/notify} 等任何旁路）——
 * 直连绕过网关 AuthFilter / 限流 / 监控，不安全。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final NotifyWebSocketHandler handler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/api/notification/ws/notify")
                .setAllowedOriginPatterns("*");
    }
}

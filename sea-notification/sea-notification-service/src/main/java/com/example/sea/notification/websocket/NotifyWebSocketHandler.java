package com.example.sea.notification.websocket;

import com.example.sea.notification.api.vo.InAppMessageVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 站内信 WebSocket 推送。
 *
 * <p>客户端连接：
 * <pre>ws://host:8085/ws/notify?userId=123</pre>
 *
 * <p>服务端用 ConcurrentHashMap&lt;Long, WebSocketSession&gt; 索引 user，
 * InAppNotifier / InAppMessageServiceImpl 推消息时调用 {@link #push(Long, Object)}。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Slf4j
@Component
public class NotifyWebSocketHandler extends TextWebSocketHandler {

    private static final String USER_ID_PARAM = "userId";

    private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long userId = parseUserId(session);
        if (userId == null) {
            try {
                session.close(CloseStatus.POLICY_VIOLATION.withReason("missing userId"));
            } catch (IOException ignored) {
            }
            return;
        }
        sessions.put(userId, session);
        log.info("ws.notify.connect userId={} sessionId={}", userId, session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = parseUserId(session);
        if (userId != null) {
            sessions.remove(userId, session);
            log.info("ws.notify.close userId={} reason={}", userId, status);
        }
    }

    /**
     * 给该用户推送一条站内信（如果在线）。
     */
    public void push(Long userId, InAppMessageVO payload) {
        WebSocketSession s = sessions.get(userId);
        if (s == null || !s.isOpen()) return;
        try {
            s.sendMessage(new TextMessage(objectMapper.writeValueAsString(payload)));
        } catch (Exception e) {
            log.warn("ws.notify.push failed userId={} err={}", userId, e.getMessage());
        }
    }

    private Long parseUserId(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri == null || uri.getQuery() == null) return null;
        for (String pair : uri.getQuery().split("&")) {
            int idx = pair.indexOf('=');
            if (idx < 0) continue;
            String key = pair.substring(0, idx);
            String val = pair.substring(idx + 1);
            if (USER_ID_PARAM.equals(key)) {
                try {
                    return Long.parseLong(val);
                } catch (NumberFormatException ignored) {
                    return null;
                }
            }
        }
        return null;
    }
}

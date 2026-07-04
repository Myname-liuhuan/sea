package com.example.sea.notification.websocket;

import com.example.sea.common.security.utils.JwtUtil;
import com.example.sea.notification.api.vo.InAppMessageVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
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
 * <pre>ws://host:8085/ws/notify?token=&lt;accessToken&gt;</pre>
 *
 * <p>安全：服务端从 JWT 的 claims 解析 userId；忽略 query 中的 userId（防伪）。
 * 若 token 无效 / 过期，握手期间直接 close(1008)。
 *
 * @author liuhuan
 * @date 2026-07-05
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotifyWebSocketHandler extends TextWebSocketHandler {

    private static final String TOKEN_PARAM = "token";

    private final JwtUtil jwtUtil;

    private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long userId = parseUserIdFromJwt(session);
        if (userId == null) {
            try {
                session.close(CloseStatus.POLICY_VIOLATION.withReason("invalid token"));
            } catch (IOException ignored) {
            }
            return;
        }
        sessions.put(userId, session);
        log.info("ws.notify.connect userId={} sessionId={}", userId, session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = parseUserIdFromJwt(session);
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

    /**
     * 解析 query 里的 token → JwtUtil.parseToken → claims.getSubject() → userId。
     */
    private Long parseUserIdFromJwt(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri == null || uri.getQuery() == null) return null;
        String token = readQuery(uri.getQuery(), TOKEN_PARAM);
        if (token == null || token.isBlank()) return null;
        try {
            Claims claims = jwtUtil.parseToken(token);
            String subject = claims.getSubject();
            if (subject == null || subject.isBlank()) return null;
            return Long.parseLong(subject);
        } catch (Exception e) {
            log.warn("ws.notify.parseToken failed: {}", e.getMessage());
            return null;
        }
    }

    private static String readQuery(String query, String key) {
        for (String pair : query.split("&")) {
            int idx = pair.indexOf('=');
            if (idx < 0) continue;
            String k = pair.substring(0, idx);
            String v = pair.substring(idx + 1);
            if (key.equals(k)) return v;
        }
        return null;
    }
}

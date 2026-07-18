package com.example.sea.notification.service;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.notification.api.dto.NotifyDTO;
import com.example.sea.notification.api.vo.NotifyVO;
import com.example.sea.notification.constants.ChannelEnum;
import com.example.sea.notification.dao.NotifyLogMapper;
import com.example.sea.notification.entity.NotifyLogPO;
import com.example.sea.notification.notifier.Notifier;
import com.example.sea.notification.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * {@link NotificationServiceImpl} 主调度单测。
 *
 * <p>验证：主通道失败 → fallback 链路接管；全部失败 → NotifyVO.failed。
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotifyLogMapper logMapper;

    static class FakeNotifier implements Notifier {
        private final ChannelEnum ch;
        private final boolean enabled;
        private final boolean sendSuccess;

        FakeNotifier(ChannelEnum ch, boolean enabled, boolean sendSuccess) {
            this.ch = ch;
            this.enabled = enabled;
            this.sendSuccess = sendSuccess;
        }

        @Override public ChannelEnum channel() { return ch; }
        @Override public boolean enabled(NotifyDTO r) { return enabled; }
        @Override public NotifyVO send(NotifyDTO r) {
            return sendSuccess
                    ? NotifyVO.success(ch.getCode(), 1L)
                    : NotifyVO.failed(ch.getCode(), null, "mock fail");
        }
    }

    @Test
    void primarySucceeds_returnsPrimaryResult() {
        List<Notifier> notifiers = List.of(
                new FakeNotifier(ChannelEnum.IN_APP, true, true),
                new FakeNotifier(ChannelEnum.EMAIL, true, true),
                new FakeNotifier(ChannelEnum.SMS, true, false));
        NotificationServiceImpl svc = new NotificationServiceImpl(notifiers, logMapper);

        NotifyDTO req = new NotifyDTO();
        req.setPrimaryChannel("IN_APP");
        req.setReceiverUserId(1L);
        req.setTemplateCode("PWD_RESET_OK");
        req.setParams(new HashMap<>());

        CommonResult<NotifyVO> resp = svc.send(req);
        assertNotNull(resp);
        assertTrue(resp.isSuccess());
        assertEquals("IN_APP", resp.getData().getChannel());
    }

    @Test
    void primaryDisabledFailsOverToFallbackEmail() {
        List<Notifier> notifiers = List.of(
                new FakeNotifier(ChannelEnum.IN_APP, false, true),
                new FakeNotifier(ChannelEnum.EMAIL, true, true),
                new FakeNotifier(ChannelEnum.SMS, true, false));
        NotificationServiceImpl svc = new NotificationServiceImpl(notifiers, logMapper);

        NotifyDTO req = new NotifyDTO();
        req.setPrimaryChannel("IN_APP");
        req.setFallbackChannels(List.of("EMAIL", "SMS"));
        req.setReceiverUserId(1L);
        req.setTemplateCode("X");
        Map<String, String> params = new HashMap<>();
        params.put("k", "v");
        req.setParams(params);

        CommonResult<NotifyVO> resp = svc.send(req);
        assertNotNull(resp);
        assertTrue(resp.isSuccess());
        assertEquals("EMAIL", resp.getData().getChannel());
    }

    @Test
    void allChannelsFailing_returnsFailedResult() {
        List<Notifier> notifiers = List.of(
                new FakeNotifier(ChannelEnum.IN_APP, true, false),
                new FakeNotifier(ChannelEnum.EMAIL, true, false),
                new FakeNotifier(ChannelEnum.SMS, true, false));
        NotificationServiceImpl svc = new NotificationServiceImpl(notifiers, logMapper);

        NotifyDTO req = new NotifyDTO();
        req.setPrimaryChannel("IN_APP");
        req.setFallbackChannels(List.of("EMAIL", "SMS"));
        req.setReceiverUserId(1L);
        req.setTemplateCode("X");
        req.setParams(new HashMap<>());
        when(logMapper.insert(any(NotifyLogPO.class))).thenReturn(1);

        CommonResult<NotifyVO> resp = svc.send(req);
        assertNotNull(resp);
        // send() 本身始终返回 CommonResult.success(data)；通道全失败由 data.success=false 表达
        assertTrue(resp.isSuccess());
        assertEquals(false, resp.getData().isSuccess());
    }
}
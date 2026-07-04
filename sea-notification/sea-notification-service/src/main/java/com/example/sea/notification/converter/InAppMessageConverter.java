package com.example.sea.notification.converter;

import com.example.sea.notification.api.vo.InAppMessageVO;
import com.example.sea.notification.entity.InAppMessagePO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 站内信 PO ↔ VO。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Mapper(componentModel = "spring")
public interface InAppMessageConverter {

    InAppMessageVO entityToVo(InAppMessagePO po);

    List<InAppMessageVO> entityListToVoList(List<InAppMessagePO> list);
}

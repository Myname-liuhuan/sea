package com.example.sea.notification.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.sea.common.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 通知模板实体。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("notify_template")
public class NotifyTemplatePO extends BaseEntity {

    /** 模板编码 e.g. PWD_RESET_OK */
    private String templateCode;

    /** 版本号 */
    private Integer version;

    /** 通道 */
    private String channel;

    /** 主题 */
    private String subject;

    /** 内容（含 ${name} 占位） */
    private String content;

    /** profile（dev/sit/prod 等） */
    private String profile;

    /** 启用 */
    private Integer enabled;
}
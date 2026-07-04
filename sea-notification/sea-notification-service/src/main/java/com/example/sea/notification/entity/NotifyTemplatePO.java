package com.example.sea.notification.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 通知模板实体。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Data
@Accessors(chain = true)
@TableName("notify_template")
public class NotifyTemplatePO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

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

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic(value = "0", delval = "1")
    private Integer delFlag;
}

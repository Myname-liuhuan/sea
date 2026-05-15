package com.example.sea.code.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "数据源信息")
public class CodegenDataSourceVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "数据源名称")
    private String name;

    @Schema(description = "数据库类型（mysql/postgresql/oracle）")
    private String dbType;

    @Schema(description = "数据库主机地址")
    private String host;

    @Schema(description = "数据库端口")
    private Integer port;

    @Schema(description = "数据库用户名")
    private String username;

    @Schema(description = "默认数据库/模式")
    private String schemaName;

    @Schema(description = "状态（0-禁用，1-启用）")
    private Boolean status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}

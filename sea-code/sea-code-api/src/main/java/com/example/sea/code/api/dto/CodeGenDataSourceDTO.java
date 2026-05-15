package com.example.sea.code.api.dto;

import com.example.sea.common.core.validation.GroupCheck;
import com.example.sea.common.core.validation.GroupInsert;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "数据源配置入参")
public class CodeGenDataSourceDTO {

    @NotBlank(message = "数据源名称不能为空", groups = {GroupInsert.class})
    @Schema(description = "数据源名称")
    private String name;

    @NotBlank(message = "数据库类型不能为空", groups = {GroupCheck.class, GroupInsert.class})
    @Schema(description = "数据库类型（mysql/postgresql/oracle）")
    private String dbType;

    @NotBlank(message = "数据库主机地址不能为空", groups = {GroupCheck.class, GroupInsert.class})
    @Schema(description = "数据库主机地址")
    private String host;

    @NotNull(message = "数据库端口不能为空", groups = {GroupCheck.class, GroupInsert.class})
    @Schema(description = "数据库端口")
    private Integer port;

    @NotBlank(message = "数据库用户名不能为空", groups = {GroupCheck.class, GroupInsert.class})
    @Schema(description = "数据库用户名")
    private String username;

    @NotBlank(message = "数据库密码不能为空", groups = {GroupCheck.class, GroupInsert.class})
    @Schema(description = "数据库密码")
    private String password;

}

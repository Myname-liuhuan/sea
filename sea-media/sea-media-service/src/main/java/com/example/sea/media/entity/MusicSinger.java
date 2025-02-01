package com.example.sea.media.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 歌手表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("d_singer")
public class MusicSinger {

    @TableId(type = IdType.ASSIGN_ID) 
    private Long id;

    //歌手名称
    private String name;

    //性别 1男 0女
    private Integer sex;

    //歌手类型 1独立歌手  2乐队
    private Integer singerType;

    //出生日期
    private Date birthday;

/*** 以下为公共字段 ***/
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.UPDATE)
    private Long updateUser;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Integer delFlag;
}

package com.kitchen.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 图片文件实体类
 * 用于将图片二进制数据存储到数据库
 */
@Data
@TableName("sys_file")
public class SysFile {

    /**
     * 主键ID，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 文件类型（MIME类型）
     */
    private String contentType;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件二进制数据
     */
    private byte[] fileData;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

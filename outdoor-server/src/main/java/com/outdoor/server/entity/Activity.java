package com.outdoor.server.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("activity")
public class Activity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long guideId;
    private String title;
    private String type;
    private Integer difficulty;
    private String destination;
    private String gatherPoint;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal price;
    private Integer maxNum;
    private Integer currentNum;
    private String cover;
    private String content;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;

    @TableField(exist = false)
    private String guideName;
    @TableField(exist = false)
    private String guideCity;
    @TableField(exist = false)
    private String guideTags;
    @TableField(exist = false)
    private String guideIntro;
    @TableField(exist = false)
    private String guideAvatar;
    @TableField(exist = false)
    private BigDecimal guidePrice;
    @TableField(exist = false)
    private Long guideActivityCount;
    @TableField(exist = false)
    private Long reviewCount;
    @TableField(exist = false)
    private Double avgRating;
}

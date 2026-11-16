package com.outdoor.server.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ActivityDTO {
    private Long id;
    private Long guideId;
    @NotBlank(message = "活动标题不能为空")
    private String title;
    @NotBlank(message = "请选择活动类型")
    private String type;
    private Integer difficulty;
    @NotBlank(message = "目的地不能为空")
    private String destination;
    private String gatherPoint;
    @NotNull(message = "请选择开始时间")
    private LocalDateTime startTime;
    @NotNull(message = "请选择结束时间")
    private LocalDateTime endTime;
    @NotNull(message = "请输入价格")
    @DecimalMin(value = "0", message = "价格不能为负数")
    private BigDecimal price;
    @NotNull(message = "请输入人数上限")
    @Min(value = 1, message = "人数上限至少为 1")
    private Integer maxNum;
    private String cover;
    private String content;
}

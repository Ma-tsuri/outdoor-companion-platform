package com.outdoor.server.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookingVO {
    private Long id;
    private Long activityId;
    private String activityTitle;
    private String activityCover;
    private Long userId;
    private String userName;
    private Long guideId;
    private String guideName;
    private LocalDate bookDate;
    private Integer num;
    private BigDecimal totalPrice;
    private Integer status;
    private LocalDateTime payTime;
    private LocalDateTime createTime;
    private Boolean reviewed;
}

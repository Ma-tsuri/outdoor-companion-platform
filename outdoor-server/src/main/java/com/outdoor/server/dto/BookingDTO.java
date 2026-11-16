package com.outdoor.server.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class BookingDTO {
    @NotNull(message = "请选择活动")
    private Long activityId;
    private LocalDate bookDate;
    @Min(value = 1, message = "预约人数至少为 1")
    private Integer num;
}

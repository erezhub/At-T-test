package com.hometest.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScreeningDto {

    private Long id;
    private String movieTitle;
    private Long theaterId;
    private String startTime;
    private String endTime;
}

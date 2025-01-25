package com.hometest.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingDto {
    private Long userId;
    private String movieTitle;
    private long theaterId;
    private String showTime;
    private Long seatNumber;
    private double price;
}

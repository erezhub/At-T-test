package com.hometest.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScreeningsOverLapException extends Throwable {

    private long screeningId;
    private String startTime;
    private String endTime;
}

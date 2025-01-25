package com.hometest.exception;

public class ScreeningNotFoundException extends Exception {

    public ScreeningNotFoundException(Long screeningId) {
        super(screeningId.toString());
    }

    public ScreeningNotFoundException(String movieTitle, String showTime, long theaterId) {
        super(movieTitle + " at " + showTime + " in " + theaterId);
    }
}

package com.hometest.exception;

public class BookingException extends Exception {

    public BookingException(long seatNumber, String movieTitle, String showTime, long theaterId) {
        super("seat not available: " + seatNumber + " for " + movieTitle + " at " + showTime + " in theater" + theaterId);
    }

    public BookingException(long seatNumber, long theaterId) {
        super("seat not found in theater: " + seatNumber + " in theater" + theaterId);
    }
}

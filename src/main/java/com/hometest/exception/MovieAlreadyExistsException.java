package com.hometest.exception;

public class MovieAlreadyExistsException extends RuntimeException {

    public MovieAlreadyExistsException(String movieTitle) {
        super(movieTitle);
    }
}

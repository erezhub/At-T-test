package com.hometest.exception;

public class MovieNotFoundException extends RuntimeException {

    public MovieNotFoundException(String movieTitle) {
        super(movieTitle);
    }
}

package com.hometest.controllers;

import com.hometest.dto.BookingDto;
import com.hometest.exception.ScreeningNotFoundException;
import com.hometest.exception.BookingException;
import com.hometest.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public void makeBooking(@RequestBody BookingDto dto){
        try {
            bookingService.bookASeat(dto);
        } catch (ScreeningNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "screening not found: " + e.getMessage());
        } catch (BookingException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
}

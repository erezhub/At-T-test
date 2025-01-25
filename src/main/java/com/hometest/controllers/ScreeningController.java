package com.hometest.controllers;

import com.hometest.dto.ScreeningDto;
import com.hometest.exception.InvalidScreeningTimeRange;
import com.hometest.exception.MovieNotFoundException;
import com.hometest.exception.ScreeningNotFoundException;
import com.hometest.exception.ScreeningsOverLapException;
import com.hometest.services.ScreeningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/screening")
@RequiredArgsConstructor
public class ScreeningController {

    private final ScreeningService screeningService;

    @PostMapping
    public Long createScreening(@RequestBody ScreeningDto dto) {
        try {
            return screeningService.createScreening(dto);
        } catch (ScreeningsOverLapException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "screening overlaps to an existing screening: screening "
                    + e.getScreeningId() + ", which starts at " + e.getStartTime() + " and ends at " + e.getEndTime());
        } catch (MovieNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "title " + e.getMessage() +  " not found");
        } catch (InvalidScreeningTimeRange e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping
    public void updateScreening(@RequestBody ScreeningDto dto) {
        try {
            screeningService.updateScreening(dto);
        } catch (ScreeningNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "screening " + e.getMessage() +  " not found");
        } catch (ScreeningsOverLapException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "screening overlaps to an existing screening: screening "
                    + e.getScreeningId() + ", which starts at " + e.getStartTime() + " and ends at " + e.getEndTime());
        } catch (MovieNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "title " + e.getMessage() +  " not found");
        } catch (InvalidScreeningTimeRange e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @DeleteMapping(value = "/{screeningId}")
    public void deleteScreening(@PathVariable Long screeningId) {
        try {
            screeningService.deleteScreening(screeningId);
        } catch (ScreeningNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "screening " + e.getMessage() +  " not found");
        }
    }

    @GetMapping(value = "/title/{movieTitle}")
    public List<ScreeningDto> getScreeningsByMovieTitle(@PathVariable String movieTitle) {
        return screeningService.findScreeningByMovie(movieTitle);
    }

    @GetMapping(value = "/theater/{theaterId}")
    public List<ScreeningDto> getScreeningsByTheater(@PathVariable long theaterId) {
        return screeningService.findScreeningByTheater(theaterId);
    }
}
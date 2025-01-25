package com.hometest.controllers;

import com.hometest.dto.MovieDto;
import com.hometest.exception.MovieAlreadyExistsException;
import com.hometest.exception.MovieNotFoundException;
import com.hometest.services.MovieService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@RequestMapping("/movie")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @PostMapping
    public void createMovie(@RequestBody MovieDto movieDto) {
        try {
            movieService.createMovie(movieDto);
        } catch (MovieAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "title " + e.getMessage() +  " already exits");
        }
    }

    @PutMapping
    public void updateMovie(@RequestBody MovieDto movieDto) {
        try {
            movieService.updateMovie(movieDto);
        } catch (MovieNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "title " + e.getMessage() +  " not found");
        }
    }

    @DeleteMapping(value = "/{movieTitle}")
    public void deleteMovie(@PathVariable String movieTitle) {
        try {
            movieService.deleteMovie(movieTitle);
        } catch (MovieNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "title " + e.getMessage() +  " not found");
        }
    }

    @GetMapping
    public List<MovieDto> getAllMovies() {
        return movieService.findAllMovies();
    }


}

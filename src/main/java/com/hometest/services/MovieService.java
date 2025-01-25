package com.hometest.services;

import com.hometest.database.entities.MovieEntity;
import com.hometest.database.repositories.MovieRepository;
import com.hometest.dto.MovieDto;
import com.hometest.exception.MovieAlreadyExistsException;
import com.hometest.exception.MovieNotFoundException;
import com.hometest.utils.IgnoreNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;

    /**
     * creates a new movie in the DB
     * @param movieDto - the movie's details
     */
    public void createMovie(MovieDto movieDto) {
        try {
            findMovieByTitle(movieDto.getTitle());
            throw new MovieAlreadyExistsException(movieDto.getTitle());
        } catch (MovieNotFoundException e) {
            MovieEntity movieEntity = new MovieEntity();
            BeanUtils.copyProperties(movieDto, movieEntity);
            movieRepository.save(movieEntity);
        }

    }

    /**
     * updates an existing movie
     * @param movieDto - the movie's details
     */
    public void updateMovie(MovieDto movieDto) {
        MovieEntity movieEntity = findMovieByTitle(movieDto.getTitle());
        BeanUtils.copyProperties(movieDto, movieEntity, IgnoreNull.getNullPropertyNames(movieDto));
        movieRepository.save(movieEntity);
    }

    /**
     * deletes a movie
     * @param title - the movie title
     */
    public void deleteMovie(String title) {
        MovieEntity movieEntity = findMovieByTitle(title);
        movieRepository.delete(movieEntity);
    }

    /**
     * find all available movies
     * @return - list of movies' details
     */
    public List<MovieDto> findAllMovies() {
        return movieRepository.findAll().stream().map(movieEntity -> {
            MovieDto movieDto = new MovieDto();
            BeanUtils.copyProperties(movieEntity, movieDto);
            return movieDto;
        }).toList();
    }

    /**
     * find a movie by title
     * @param title - the movie's title
     * @return movie details
     */
    public MovieDto findByTitle(String title) {
        Optional<MovieEntity> optionalMovie = movieRepository.findById(title);
        if (optionalMovie.isEmpty())
            return null;
        MovieDto movieDto = new MovieDto();
        BeanUtils.copyProperties(optionalMovie.get(), movieDto);
        return movieDto;
    }

    // TODO - add find by feature

    /*-- private methods --*/

    private MovieEntity findMovieByTitle(String title) throws MovieNotFoundException {
        Optional<MovieEntity> optionalMovie = movieRepository.findById(title);
        if (optionalMovie.isEmpty()) {
            throw new MovieNotFoundException(title);
        }
        return optionalMovie.get();
    }
}

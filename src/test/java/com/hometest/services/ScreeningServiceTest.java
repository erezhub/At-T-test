package com.hometest.services;

import com.hometest.database.entities.ScreeningEntity;
import com.hometest.database.repositories.ScreeningRepository;
import com.hometest.dto.MovieDto;
import com.hometest.dto.ScreeningDto;
import com.hometest.exception.InvalidScreeningTimeRange;
import com.hometest.exception.MovieNotFoundException;
import com.hometest.exception.ScreeningNotFoundException;
import com.hometest.exception.ScreeningsOverLapException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.hometest.services.ScreeningService.formatter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScreeningServiceTest {

    private ScreeningService screeningService;
    @Mock
    private ScreeningRepository screeningRepository;
    @Mock
    private MovieService movieService;

    @BeforeEach
    void setUp() {
        screeningService = new ScreeningService(screeningRepository, movieService);
        screeningService.init();
    }

    @Test
    void createScreening() throws InvalidScreeningTimeRange, ScreeningsOverLapException {
        ScreeningDto dto = ScreeningDto.builder().movieTitle("title").theaterId(1L).startTime("06/06/2006 15:20").endTime("06/06/2006 17:00").build();
        ScreeningEntity screeningEntity = new ScreeningEntity();
        screeningEntity.setId(1L);
        when(screeningRepository.save(any())).thenReturn(screeningEntity);
        when(movieService.findByTitle(dto.getMovieTitle())).thenReturn(new MovieDto());
        long screeningId = screeningService.createScreening(dto);

        assertEquals(1L, screeningId);
    }

    @Test
    void createScreening_invalidStartTime() {
        ScreeningDto dto = ScreeningDto.builder().movieTitle("title").theaterId(1L).startTime("06/06/2006 25:20").endTime("06/06/2006 17:00").build();

        assertThrows(InvalidScreeningTimeRange.class, () -> screeningService.createScreening(dto), "invalid start-time: " + dto.getStartTime());
    }

    @Test
    void createScreening_invalidTimeRange() {
        ScreeningDto dto = ScreeningDto.builder().movieTitle("title").theaterId(1L).startTime("06/06/2006 15:20").endTime("06/06/2006 15:00").build();

        assertThrows(InvalidScreeningTimeRange.class, () -> screeningService.createScreening(dto),"invalid time-range: start-time is before end-time");
    }

    @Test
    void createScreening_overlap() {
        ScreeningDto dto = ScreeningDto.builder().movieTitle("title").theaterId(1L).startTime("06/06/2006 15:20").endTime("06/06/2006 17:00").build();
        LocalDateTime startTime = LocalDateTime.parse("06/06/2006 15:00", formatter);
        LocalDateTime endTime = LocalDateTime.parse("06/06/2006 16:00", formatter);
        when(screeningRepository.findByTheaterId(1L)).thenReturn(List.of(ScreeningEntity.builder().id(1L).startTime(startTime).endTime(endTime).build()));

        assertThrows(ScreeningsOverLapException.class, () -> screeningService.createScreening(dto));
    }

    @Test
    void createScreening_movieNotFound() {
        ScreeningDto dto = ScreeningDto.builder().movieTitle("title").theaterId(1L).startTime("06/06/2006 15:20").endTime("06/06/2006 17:00").build();
        when(movieService.findByTitle(dto.getMovieTitle())).thenThrow(new MovieNotFoundException(dto.getMovieTitle()));

        assertThrows(MovieNotFoundException.class, () -> screeningService.createScreening(dto));
    }

    @Test
    void updateScreening_notFound() {
        ScreeningDto dto = ScreeningDto.builder().id(1L).movieTitle("title").theaterId(1L).startTime("06/06/2006 15:20").endTime("06/06/2006 17:00").build();
        when(screeningRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ScreeningNotFoundException.class, () -> screeningService.updateScreening(dto));
    }
}
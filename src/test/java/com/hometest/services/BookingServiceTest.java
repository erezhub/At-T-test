package com.hometest.services;

import com.hometest.database.entities.BookingEntity;
import com.hometest.database.repositories.BookingRepository;
import com.hometest.dto.BookingDto;
import com.hometest.dto.ScreeningDto;
import com.hometest.exception.BookingException;
import com.hometest.exception.ScreeningNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    private BookingService bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ScreeningService screeningService;
    @Mock
    private TheaterService theaterService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService(bookingRepository, screeningService, theaterService);
        bookingService.init();
    }

    @Test
    void bookASeat() throws ScreeningNotFoundException {
        BookingDto bookingDto = BookingDto.builder().seatNumber(1L).price(10.0).userId(1L).showTime("06/07/2008 15:00").theaterId(1L).movieTitle("title").build();
        ScreeningDto screeningDto = ScreeningDto.builder().id(1L).build();
        when(screeningService.findScreeningByTheaterAndTime(bookingDto.getTheaterId(), bookingDto.getShowTime(), bookingDto.getMovieTitle()))
                .thenReturn(screeningDto);
        when(theaterService.getNumberOfSeatsInTheater(bookingDto.getTheaterId())).thenReturn(100);
        when(bookingRepository.findByScreeningIdAndSeatNumber(screeningDto.getId(), bookingDto.getSeatNumber())).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> bookingService.bookASeat(bookingDto));
    }

    @Test
    void bookASeat_screeningNotFound() throws ScreeningNotFoundException {
        BookingDto bookingDto = BookingDto.builder().seatNumber(1L).price(10.0).userId(1L).showTime("06/07/2008 15:00").theaterId(1L).movieTitle("title").build();
        when(screeningService.findScreeningByTheaterAndTime(bookingDto.getTheaterId(), bookingDto.getShowTime(), bookingDto.getMovieTitle()))
                .thenThrow(new ScreeningNotFoundException(bookingDto.getMovieTitle(), bookingDto.getShowTime(), bookingDto.getTheaterId()));

        assertThrows(ScreeningNotFoundException.class, () -> bookingService.bookASeat(bookingDto));

    }

    @Test
    void bookASeat_invalidSeat() throws ScreeningNotFoundException {
        BookingDto bookingDto = BookingDto.builder().seatNumber(100L).price(10.0).userId(1L).showTime("06/07/2008 15:00").theaterId(1L).movieTitle("title").build();
        when(screeningService.findScreeningByTheaterAndTime(bookingDto.getTheaterId(), bookingDto.getShowTime(), bookingDto.getMovieTitle())).thenReturn(any());
        when(theaterService.getNumberOfSeatsInTheater(bookingDto.getTheaterId())).thenReturn(50);

        assertThrows(BookingException.class, () -> bookingService.bookASeat(bookingDto), "seat not found in theater: " + bookingDto.getSeatNumber() + " in theater" + bookingDto.getTheaterId());
    }

    @Test
    void bookASeat_seatNotAvailable() throws ScreeningNotFoundException {
        BookingDto bookingDto = BookingDto.builder().seatNumber(1L).price(10.0).userId(1L).showTime("06/07/2008 15:00").theaterId(1L).movieTitle("title").build();
        ScreeningDto screeningDto = ScreeningDto.builder().id(1L).build();
        when(screeningService.findScreeningByTheaterAndTime(bookingDto.getTheaterId(), bookingDto.getShowTime(), bookingDto.getMovieTitle()))
                .thenReturn(screeningDto);
        when(theaterService.getNumberOfSeatsInTheater(bookingDto.getTheaterId())).thenReturn(100);
        when(bookingRepository.findByScreeningIdAndSeatNumber(screeningDto.getId(), bookingDto.getSeatNumber())).thenReturn(Optional.of(BookingEntity.builder().build()));

        assertThrows(BookingException.class, () -> bookingService.bookASeat(bookingDto),
                "seat not available: " + bookingDto.getSeatNumber() + " for " + bookingDto.getMovieTitle() + " at " + bookingDto.getShowTime() + " in theater" + bookingDto.getTheaterId());
    }
}
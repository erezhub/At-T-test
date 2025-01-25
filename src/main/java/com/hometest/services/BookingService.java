package com.hometest.services;

import com.hometest.database.entities.BookingEntity;
import com.hometest.database.repositories.BookingRepository;
import com.hometest.dto.BookingDto;
import com.hometest.dto.ScreeningDto;
import com.hometest.exception.ScreeningNotFoundException;
import com.hometest.exception.BookingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;



@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ScreeningService screeningService;
    private final TheaterService theaterService;
    private Lock writeLock;

    @PostConstruct
    public void init() {
        ReadWriteLock lock = new ReentrantReadWriteLock();
        writeLock = lock.writeLock();
    }

    /**
     * makes a booking of a movie
     * @param bookingDto - booking's details
     * @throws ScreeningNotFoundException - screening not found
     * @throws BookingException - either seat is not found or not available
     */
    public void bookASeat(BookingDto bookingDto) throws ScreeningNotFoundException, BookingException {
        ScreeningDto screeningDto = screeningService.findScreeningByTheaterAndTime(bookingDto.getTheaterId(), bookingDto.getShowTime(), bookingDto.getMovieTitle());
        int numberOfSeatsInTheater = theaterService.getNumberOfSeatsInTheater(bookingDto.getTheaterId());
        if (bookingDto.getSeatNumber() > numberOfSeatsInTheater) {
            throw new BookingException(bookingDto.getSeatNumber(), bookingDto.getTheaterId());
        }
        try {
            writeLock.lock();
            if (bookingRepository.findByScreeningIdAndSeatNumber(screeningDto.getId(), bookingDto.getSeatNumber()).isEmpty()) {
                BookingEntity bookingEntity = BookingEntity.builder()
                        .seatNumber(bookingDto.getSeatNumber())
                        .price(bookingDto.getPrice())
                        .screeningId(screeningDto.getId())
                        .userId(bookingDto.getUserId())
                        .build();
                bookingRepository.save(bookingEntity);
            } else {
                throw new BookingException(bookingDto.getSeatNumber(), bookingDto.getMovieTitle(), bookingDto.getShowTime(), bookingDto.getTheaterId());
            }
        } finally {
            writeLock.unlock();
        }
    }
}

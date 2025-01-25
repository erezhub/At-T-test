package com.hometest.services;

import com.hometest.database.entities.TheaterEntity;
import com.hometest.database.repositories.TheaterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
@RequiredArgsConstructor
public class TheaterService {

    private final TheaterRepository theaterRepository;
    private final Map<Long, Integer> theatersSeatingsMap = new HashMap<>();

    @Value("${theaters.seats}")
    private List<Integer> theaterSeats;

    /*-- public methods --*/

    @PostConstruct
    public void init() {
        theaterSeats.forEach(this::createTheater);

    }

    /**
     * get number of seats in a theater
     * @param theaterId
     * @return - number of seats in a theater
     */
    public int getNumberOfSeatsInTheater(long theaterId) {
        return theatersSeatingsMap.get(theaterId);
    }



    /*-- private methods --*/

    private void createTheater(int numberOfSeats) {
        TheaterEntity theater = TheaterEntity.builder().numberOfSeats(numberOfSeats).build();
        TheaterEntity saved = theaterRepository.save(theater);
        theatersSeatingsMap.put(saved.getId(), numberOfSeats);
    }
}

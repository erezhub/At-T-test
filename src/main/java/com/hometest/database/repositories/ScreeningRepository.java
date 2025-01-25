package com.hometest.database.repositories;

import com.hometest.database.entities.ScreeningEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScreeningRepository extends JpaRepository<ScreeningEntity, Long> {

    List<ScreeningEntity> findByMovieTitle(String movieTitle);
    List<ScreeningEntity> findByTheaterId(long theaterId);
    Optional<ScreeningEntity> findByStartTimeAndTheaterIdAndMovieTitle(LocalDateTime startTime, long theaterId, String movieTitle);
}

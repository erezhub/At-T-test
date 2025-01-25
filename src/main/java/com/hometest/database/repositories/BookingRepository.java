package com.hometest.database.repositories;

import com.hometest.database.entities.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    Optional<BookingEntity> findByScreeningIdAndSeatNumber(long screeningId, long seatNumber);
}

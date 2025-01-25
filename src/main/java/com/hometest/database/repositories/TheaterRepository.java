package com.hometest.database.repositories;

import com.hometest.database.entities.TheaterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TheaterRepository extends JpaRepository<TheaterEntity, Long> {
}

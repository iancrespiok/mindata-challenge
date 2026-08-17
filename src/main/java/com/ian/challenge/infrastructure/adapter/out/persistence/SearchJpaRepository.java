package com.ian.challenge.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface SearchJpaRepository extends JpaRepository<SearchEntity, String> {
    @Query("select count(s) from SearchEntity s "
            + "where s.hotelId = :hotelId and s.checkIn = :checkIn "
            + "and s.checkOut = :checkOut and s.agesKey = :agesKey")
    long countByCriteria(@Param("hotelId") String hotelId, @Param("checkIn") LocalDate checkIn,
                         @Param("checkOut") LocalDate checkOut, @Param("agesKey") String agesKey);
}

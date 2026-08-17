package com.ian.challenge.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "HOTEL_SEARCH",
        indexes = @jakarta.persistence.Index(
                name = "IDX_SEARCH_CRITERIA",
                columnList = "HOTEL_ID, CHECK_IN, CHECK_OUT, AGES_KEY"
        ))
@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class SearchEntity {
        @Id
        @Column(name = "SEARCH_ID", length = 36, nullable = false)
        private final String searchId;

        @Column(name = "HOTEL_ID", nullable = false, length = 64)
        private final String hotelId;

        @Column(name = "CHECK_IN", nullable = false)
        private final LocalDate checkIn;

        @Column(name = "CHECK_OUT", nullable = false)
        private final LocalDate checkOut;

        /** Edades separadas por coma, preservando el orden original (ver clase). */
        @Column(name = "AGES_KEY", nullable = false, length = 512)
        private final String agesKey;

        @Column(name = "REGISTERED_AT", nullable = false)
        private final Instant registeredAt;
}

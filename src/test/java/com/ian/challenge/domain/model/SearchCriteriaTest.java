package com.ian.challenge.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchCriteriaTest {

    private static final LocalDate CHECK_IN = LocalDate.now().plusDays(30);
    private static final LocalDate CHECK_OUT = LocalDate.now().plusDays(32);

    @Test
    void agesListIsImmutableAndDefensivelyCopied() {
        List<Integer> mutableAges = new ArrayList<>(List.of(30, 29, 1, 3));
        SearchCriteria criteria = new SearchCriteria("1234aBc", CHECK_IN, CHECK_OUT, mutableAges);
        mutableAges.add(99);

        List<Integer> unmodifiableAges = criteria.ages();
        assertAll("la lista de edades es inmutable y esta desacoplada de la original",
                () -> assertEquals(List.of(30, 29, 1, 3), unmodifiableAges),
                () -> assertThrows(UnsupportedOperationException.class, () -> unmodifiableAges.add(1))
        );
    }

    @Test
    void rejectsBlankHotelId() {
        List<Integer> ages = List.of(1);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new SearchCriteria("   ", CHECK_IN, CHECK_OUT, ages));
        assertTrue(ex.getMessage().contains("Hotel"));
    }

    @Test
    void rejectsEmptyAges() {
        List<Integer> emptyAges = List.of();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new SearchCriteria("hotel", CHECK_IN, CHECK_OUT, emptyAges));
        assertTrue(ex.getMessage().contains("Ages"));
    }

    @Test
    void rejectsNegativeAge() {
        List<Integer> negativeAges = Arrays.asList(30, -1);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new SearchCriteria("hotel", CHECK_IN, CHECK_OUT, negativeAges));
        assertTrue(ex.getMessage().contains("0"));
    }

    @Test
    void rejectsCheckInEqualToCheckOut() {
        List<Integer> ages = List.of(1);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new SearchCriteria("hotel", CHECK_IN, CHECK_IN, ages));
        assertTrue(ex.getMessage().contains("Check in"));
    }

    @Test
    void rejectsCheckInAfterCheckOut() {
        List<Integer> ages = List.of(1);
        assertThrows(IllegalArgumentException.class,
                () -> new SearchCriteria("hotel", CHECK_OUT, CHECK_IN, ages));
    }

    @Test
    void acceptsCheckInStrictlyBeforeCheckOut() {
        SearchCriteria criteria = new SearchCriteria("hotel", CHECK_IN, CHECK_OUT, List.of(1));
        assertAll(
                () -> assertEquals(CHECK_IN, criteria.checkIn()),
                () -> assertEquals(CHECK_OUT, criteria.checkOut())
        );
    }

    @Test
    void rejectsPastCheckIn() {
        LocalDate pastCheckIn = LocalDate.now().minusDays(1);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new SearchCriteria("hotel", pastCheckIn, CHECK_OUT, List.of(1)));
        assertTrue(ex.getMessage().contains("Check in"));
    }

    @Test
    void acceptsCheckInEqualToToday() {
        LocalDate today = LocalDate.now();
        SearchCriteria criteria = new SearchCriteria("hotel", today, today.plusDays(1), List.of(1));
        assertEquals(today, criteria.checkIn());
    }
}
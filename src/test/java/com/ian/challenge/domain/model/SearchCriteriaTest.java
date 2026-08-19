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

        assertAll("la lista de edades es inmutable y esta desacoplada de la original",
                () -> assertEquals(List.of(30, 29, 1, 3), criteria.ages()),
                () -> assertThrows(UnsupportedOperationException.class, () -> criteria.ages().add(1))
        );
    }

    @Test
    void agesKeyPreservesOrderWithoutStringConcatenationInLoop() {
        SearchCriteria criteria = new SearchCriteria("hotel", CHECK_IN, CHECK_OUT, List.of(30, 29, 1, 3));
        assertEquals("30,29,1,3", criteria.agesKey());
    }

    @Test
    void differentOrderProducesDifferentKey() {
        SearchCriteria a = new SearchCriteria("hotel", CHECK_IN, CHECK_OUT, List.of(1, 2));
        SearchCriteria b = new SearchCriteria("hotel", CHECK_IN, CHECK_OUT, List.of(2, 1));
        assertNotEquals(a.agesKey(), b.agesKey());
    }

    @Test
    void rejectsNullFields() {
        List<Integer> ages = List.of(1);
        assertAll("ningun campo obligatorio puede ser nulo",
                () -> assertThrows(NullPointerException.class,
                        () -> new SearchCriteria(null, CHECK_IN, CHECK_OUT, ages)),
                () -> assertThrows(NullPointerException.class,
                        () -> new SearchCriteria("h", null, CHECK_OUT, ages)),
                () -> assertThrows(NullPointerException.class,
                        () -> new SearchCriteria("h", CHECK_IN, null, ages)),
                () -> assertThrows(NullPointerException.class,
                        () -> new SearchCriteria("h", CHECK_IN, CHECK_OUT, null))
        );
    }

    @Test
    void rejectsBlankHotelId() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new SearchCriteria("   ", CHECK_IN, CHECK_OUT, List.of(1)));
        assertTrue(ex.getMessage().contains("Hotel"));
    }

    @Test
    void rejectsEmptyAges() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new SearchCriteria("hotel", CHECK_IN, CHECK_OUT, List.of()));
        assertTrue(ex.getMessage().contains("Ages"));
    }

    @Test
    void rejectsNegativeAge() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new SearchCriteria("hotel", CHECK_IN, CHECK_OUT, Arrays.asList(30, -1)));
        assertTrue(ex.getMessage().contains("0"));
    }

    @Test
    void acceptsZeroAsAValidAge() {
        SearchCriteria criteria = new SearchCriteria("hotel", CHECK_IN, CHECK_OUT, List.of(0));
        assertEquals("0", criteria.agesKey());
    }

    @Test
    void rejectsCheckInEqualToCheckOut() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new SearchCriteria("hotel", CHECK_IN, CHECK_IN, List.of(1)));
        assertTrue(ex.getMessage().contains("Check in"));
    }

    @Test
    void rejectsCheckInAfterCheckOut() {
        assertThrows(IllegalArgumentException.class,
                () -> new SearchCriteria("hotel", CHECK_OUT, CHECK_IN, List.of(1)));
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
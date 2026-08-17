package com.ian.challenge.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SearchIdTest {

    @Test
    void generatesNonBlankUniqueIds() {
        SearchId a = SearchId.generate();
        SearchId b = SearchId.generate();
        assertAll("generated ids are valids and unique",
                () -> assertFalse(a.value().isBlank()),
                () -> assertNotEquals(a, b)
        );
    }

    @Test
    void rejectsBlankValue() {
        assertThrows(IllegalArgumentException.class, () -> new SearchId(" "));
    }

    @Test
    void rejectsNullValue() {
        assertThrows(NullPointerException.class, () -> new SearchId(null));
    }
}

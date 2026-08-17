package com.ian.challenge.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SearchNotFoundExceptionTest {

    @Test
    void messageContainsSearchId() {
        SearchNotFoundException ex = new SearchNotFoundException("abc-123");
        assertThat(ex.getMessage()).contains("abc-123");
    }
}
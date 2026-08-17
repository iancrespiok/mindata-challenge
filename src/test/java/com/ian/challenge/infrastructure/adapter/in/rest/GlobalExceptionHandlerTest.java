package com.ian.challenge.infrastructure.adapter.in.rest;

import com.ian.challenge.domain.exception.SearchNotFoundException;
import com.ian.challenge.infrastructure.adapter.in.rest.exception.ErrorResponse;
import com.ian.challenge.infrastructure.adapter.in.rest.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handlesNotFound() {
        ResponseEntity<ErrorResponse> response = handler.handleNotFound(new SearchNotFoundException("id-1"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().messages()).anyMatch(m -> m.contains("id-1"));
    }

    @Test
    void handlesDateParseError() {
        DateTimeParseException ex = null;
        try {
            DateTimeFormatter.ofPattern("dd/MM/yyyy").parse("31-12-2023");
        } catch (DateTimeParseException parseException) {
            ex = parseException;
        }
        ResponseEntity<ErrorResponse> response = handler.handleDateParse(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void handlesIllegalArgument() {
        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(new IllegalArgumentException("bad value"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().messages()).containsExactly("bad value");
    }
}

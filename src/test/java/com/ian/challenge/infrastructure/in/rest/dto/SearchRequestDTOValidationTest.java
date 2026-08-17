package com.ian.challenge.infrastructure.in.rest.dto;

import com.ian.challenge.Fixture;
import com.ian.challenge.infrastructure.adapter.in.rest.dto.SearchRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SearchRequestDTOValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void validRequestHasNoViolations() {
        SearchRequestDTO dto = Fixture.defaultRequestDto();
        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void blankHotelIdIsRejected() {
        SearchRequestDTO dto = new SearchRequestDTO("", "29/12/2023", "31/12/2023", List.of(1));
        Set<ConstraintViolation<SearchRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("hotelId"));
    }

    @Test
    void malformedDateIsRejected() {
        SearchRequestDTO dto = new SearchRequestDTO("hotel", "2023-12-29", "31/12/2023", List.of(1));
        Set<ConstraintViolation<SearchRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("checkIn"));
    }

    @Test
    void emptyAgesIsRejected() {
        SearchRequestDTO dto = new SearchRequestDTO("hotel", "29/12/2023", "31/12/2023", List.of());
        Set<ConstraintViolation<SearchRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("ages"));
    }

    @Test
    void negativeAgeIsRejected() {
        SearchRequestDTO dto = new SearchRequestDTO("hotel", "29/12/2023", "31/12/2023", List.of(-1));
        Set<ConstraintViolation<SearchRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
    }
}

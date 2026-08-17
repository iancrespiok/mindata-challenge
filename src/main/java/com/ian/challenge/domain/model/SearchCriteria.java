package com.ian.challenge.domain.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public record SearchCriteria(String hotelId, LocalDate checkIn, LocalDate checkOut, List<Integer> ages) {

    public SearchCriteria {
        Objects.requireNonNull(hotelId, "Hotel cannot be null.");
        Objects.requireNonNull(checkIn, "Check in date cannot be null.");
        Objects.requireNonNull(checkOut, "Check out date cannot be null.");
        Objects.requireNonNull(ages, "Ages cannot be null.");

        if (hotelId.isBlank()) {
            throw new IllegalArgumentException("Hotel cannot be empty.");
        }
        if (ages.isEmpty()) {
            throw new IllegalArgumentException("Ages cannot be empty.");
        }
        if (!checkIn.isBefore(checkOut)) {
            throw new IllegalArgumentException("Check in date must be before check out date.");
        }
        for (Integer age : ages) {
            if (age == null) {
                throw new IllegalArgumentException("Ages cannot contain null values.");
            }
            if (age < 0) {
                throw new IllegalArgumentException("No age cannot be less than 0.");
            }
        }

        ages = List.copyOf(ages);
    }

    public String agesKey() {
        StringBuilder builder = new StringBuilder(ages.size() * 4);
        for (int i = 0; i < ages.size(); i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(ages.get(i));
        }
        return builder.toString();
    }
}

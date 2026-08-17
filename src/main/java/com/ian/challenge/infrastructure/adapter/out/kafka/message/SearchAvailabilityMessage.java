package com.ian.challenge.infrastructure.adapter.out.kafka.message;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

public record SearchAvailabilityMessage (
    String searchId,
    String hotelId,
    String checkIn,
    String checkOut,
    List<Integer> ages,
    Instant registeredAt
) implements Serializable {
}

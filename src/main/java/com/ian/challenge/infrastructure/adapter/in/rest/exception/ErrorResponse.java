package com.ian.challenge.infrastructure.adapter.in.rest.exception;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(Instant timestamp, int status, String error, List<String> messages) {
}

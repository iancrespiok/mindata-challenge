package com.ian.challenge.domain.model;

import java.util.Objects;
import java.util.UUID;

public record SearchId(String value) {
    public SearchId {
        Objects.requireNonNull(value, "El id de la busqueda no puede ser nulo");
        if (value.isBlank()) {
            throw new IllegalArgumentException("El id de la busqueda no puede estar vacio");
        }
    }

    public static SearchId generate() {
        return new SearchId(UUID.randomUUID().toString());
    }
}

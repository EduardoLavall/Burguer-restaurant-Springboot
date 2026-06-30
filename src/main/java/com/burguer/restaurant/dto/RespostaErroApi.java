package com.burguer.restaurant.dto;

import java.time.OffsetDateTime;
import java.util.Map;

public record RespostaErroApi(
        OffsetDateTime timestamp,
        int status,
        String error,
        String message,
        Map<String, String> fieldErrors) {
}

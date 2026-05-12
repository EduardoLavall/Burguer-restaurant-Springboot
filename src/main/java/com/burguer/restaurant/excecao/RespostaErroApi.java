package com.burguer.restaurant.excecao;

import java.time.OffsetDateTime;
import java.util.Map;

public record RespostaErroApi(
        OffsetDateTime dataHora,
        int status,
        String erro,
        String mensagem,
        Map<String, String> errosCampos) {
}

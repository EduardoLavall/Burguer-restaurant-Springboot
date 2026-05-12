package com.burguer.restaurant.controlador;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/saude")
public class SaudeControlador {

    @GetMapping
    public ResponseEntity<Map<String, String>> buscarSaude() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "servico", "burguer-restaurant"));
    }
}

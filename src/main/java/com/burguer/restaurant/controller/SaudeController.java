package com.burguer.restaurant.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/saude")
public class SaudeController {

    @GetMapping
    public Map<String, String> buscarSaude() {
        return Map.of(
                "status", "UP",
                "servico", "burguer-restaurant");
    }
}

package com.mifica.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    @Value("${app.security.dev-mode}")
    private boolean devMode;

    @GetMapping("/dev-mode")
    public boolean isDevMode() {
        return devMode;
    }
}

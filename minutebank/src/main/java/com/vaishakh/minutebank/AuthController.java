package com.vaishakh.minutebank;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> register(@Valid @RequestBody AuthRequest req) {
        authService.register(req);
        return Map.of("message", "Registered");
    }

    @PostMapping("/login")
    public Map<String, String> login(@Valid @RequestBody AuthRequest req) {
        return Map.of("token", authService.login(req));
    }
}
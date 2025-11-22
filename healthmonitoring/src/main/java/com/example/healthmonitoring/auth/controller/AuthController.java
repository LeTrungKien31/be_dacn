package com.example.healthmonitoring.auth.controller;

import com.example.healthmonitoring.auth.entity.User;
import com.example.healthmonitoring.auth.repo.UserRepository;
import com.example.healthmonitoring.security.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder pe;
    private final JwtService jwt;

    public AuthController(UserRepository u, PasswordEncoder p, JwtService j) {
        users = u;
        pe = p;
        jwt = j;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> register(@Valid @RequestBody RegisterReq req) {
        if (users.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Email exists");
        }
        var u = users.save(User.builder()
                .email(req.getEmail())
                .password(pe.encode(req.getPassword()))
                .fullName(req.getFullName())
                .roles("USER")
                .build());
        String token = jwt.generate(u.getEmail(), Map.of("uid", u.getId().toString(), "name", u.getFullName()));
        // Return "token" to match Flutter frontend expectation
        return Map.of("token", token);
    }

    @PostMapping("/login")
    public Map<String, String> login(@Valid @RequestBody LoginReq req) {
        var u = users.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!pe.matches(req.getPassword(), u.getPassword())) {
            throw new RuntimeException("Wrong password");
        }
        String token = jwt.generate(u.getEmail(), Map.of("uid", u.getId().toString(), "name", u.getFullName()));
        // Return "token" to match Flutter frontend expectation
        return Map.of("token", token);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RegisterReq {
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;

        @NotBlank(message = "Full name is required")
        private String fullName;

        // Alias for "fullname" (lowercase) from Flutter
        public void setFullname(String fullname) {
            this.fullName = fullname;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class LoginReq {
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;
    }
}
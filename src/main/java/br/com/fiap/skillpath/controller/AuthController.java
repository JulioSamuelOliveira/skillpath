package br.com.fiap.skillpath.controller;

import br.com.fiap.skillpath.dto.*;
import br.com.fiap.skillpath.model.User;
import br.com.fiap.skillpath.repository.UserRepository;
import br.com.fiap.skillpath.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager,
                          JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    public ResponseEntity<LoginResponse> signup(@RequestBody SignupRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        user = userRepository.save(user);

        String token = generateTokenForUser(user);
        UserDto dto = new UserDto(user.getId(), user.getName(), user.getEmail());

        return ResponseEntity
                .created(URI.create("/api/users/" + user.getId()))
                .body(new LoginResponse(token, dto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalStateException("User not found after auth"));

        String token = generateTokenForUser(user);
        UserDto dto = new UserDto(user.getId(), user.getName(), user.getEmail());

        return ResponseEntity.ok(new LoginResponse(token, dto));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String token = authHeader.substring(7);

        Claims claims;
        try {
            claims = jwtService.parseToken(token);
        } catch (JwtException | IllegalArgumentException ex) {
            return ResponseEntity.status(401).build();
        }

        Long userId = claims.get("userId", Long.class);
        String email = claims.getSubject();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (!user.getId().equals(userId)) {
            return ResponseEntity.status(401).build();
        }

        UserDto dto = new UserDto(user.getId(), user.getName(), user.getEmail());
        return ResponseEntity.ok(dto);
    }

    private String generateTokenForUser(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        return jwtService.generateToken(user.getEmail(), claims);
    }
}

package com.personalapplication.controller;

import com.personalapplication.domain.User;
import com.personalapplication.repository.UserRepository;
import com.personalapplication.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Login endpoint
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Invalid username or password!"));
        }
    }

    /**
     * Register endpoint
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user
        User user = new User(
                signupRequest.getUsername(),
                passwordEncoder.encode(signupRequest.getPassword()),
                signupRequest.getEmail()
        );

        // Add security question if provided
        if (signupRequest.getSecurityQuestion() != null && signupRequest.getSecurityAnswer() != null) {
            user.setSecurityQuestion(signupRequest.getSecurityQuestion());
            user.setSecurityAnswer(passwordEncoder.encode(signupRequest.getSecurityAnswer().toLowerCase().trim()));
        }

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    /**
     * Check if token is valid
     * GET /api/auth/validate
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.isTokenValid(token)) {
                String username = jwtUtil.extractUsername(token);
                return ResponseEntity.ok(new ValidationResponse(true, username));
            }
        }
        return ResponseEntity.ok(new ValidationResponse(false, null));
    }

    /**
     * Get security question for username
     * POST /api/auth/security-question
     */
    @PostMapping("/security-question")
    public ResponseEntity<?> getSecurityQuestion(@Valid @RequestBody UsernameRequest request) {
        User user = userRepository.findByUsername(request.getUsername()).orElse(null);

        if (user == null || user.getSecurityQuestion() == null) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: User not found or no security question set!"));
        }

        return ResponseEntity.ok(new SecurityQuestionResponse(user.getSecurityQuestion()));
    }

    /**
     * Reset password using security question
     * POST /api/auth/reset-password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        User user = userRepository.findByUsername(request.getUsername()).orElse(null);

        if (user == null || user.getSecurityQuestion() == null) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: User not found or no security question set!"));
        }

        // Check if security answer matches (case-insensitive)
        if (!passwordEncoder.matches(request.getSecurityAnswer().toLowerCase().trim(), user.getSecurityAnswer())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Incorrect security answer!"));
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Password reset successfully!"));
    }

    // DTO Classes
    public static class LoginRequest {
        @NotBlank
        @Size(min = 3, max = 20)
        private String username;

        @NotBlank
        @Size(min = 6, max = 40)
        private String password;

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class SignupRequest {
        @NotBlank
        @Size(min = 3, max = 20)
        private String username;

        @NotBlank
        @Size(max = 50)
        @Email
        private String email;

        @NotBlank
        @Size(min = 6, max = 40)
        private String password;

        private String securityQuestion;
        private String securityAnswer;

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getSecurityQuestion() { return securityQuestion; }
        public void setSecurityQuestion(String securityQuestion) { this.securityQuestion = securityQuestion; }

        public String getSecurityAnswer() { return securityAnswer; }
        public void setSecurityAnswer(String securityAnswer) { this.securityAnswer = securityAnswer; }
    }

    public static class JwtResponse {
        private String token;
        private String type = "Bearer";
        private String username;

        public JwtResponse(String accessToken, String username) {
            this.token = accessToken;
            this.username = username;
        }

        // Getters and setters
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
    }

    public static class UsernameRequest {
        @NotBlank
        private String username;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
    }

    public static class PasswordResetRequest {
        @NotBlank
        private String username;

        @NotBlank
        private String securityAnswer;

        @NotBlank
        @Size(min = 6, max = 40)
        private String newPassword;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getSecurityAnswer() { return securityAnswer; }
        public void setSecurityAnswer(String securityAnswer) { this.securityAnswer = securityAnswer; }

        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    public static class SecurityQuestionResponse {
        private String securityQuestion;

        public SecurityQuestionResponse(String securityQuestion) {
            this.securityQuestion = securityQuestion;
        }

        public String getSecurityQuestion() { return securityQuestion; }
        public void setSecurityQuestion(String securityQuestion) { this.securityQuestion = securityQuestion; }
    }

    public static class MessageResponse {
        private String message;

        public MessageResponse(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class ValidationResponse {
        private boolean valid;
        private String username;

        public ValidationResponse(boolean valid, String username) {
            this.valid = valid;
            this.username = username;
        }

        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
    }
}
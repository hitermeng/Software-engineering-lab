package com.hit.articlemanager.controller;

import com.hit.articlemanager.model.User;
import com.hit.articlemanager.service.AuthService;
import com.hit.articlemanager.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;
  private final JwtUtil jwtUtil;
  
  @Autowired
  public AuthController(AuthService authService, JwtUtil jwtUtil) {
    this.authService = authService;
    this.jwtUtil = jwtUtil;
  }
  
  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@RequestBody User user) {
    try {
      User registeredUser = authService.register(user);
      return ResponseEntity.ok(registeredUser);
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
  
  @PostMapping("/login")
  public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
    try {
      String token = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
      return ResponseEntity.ok(new AuthResponse(token));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Login failed: " + e.getMessage());
    }
  }
  
  // Inner classes for request/response
  static class LoginRequest {
    private String email;
    private String password;
    
    // Getters and setters
    public String getEmail() {
      return email;
    }
    
    public void setEmail(String email) {
      this.email = email;
    }
    
    public String getPassword() {
      return password;
    }
    
    public void setPassword(String password) {
      this.password = password;
    }
  }
  
  static class AuthResponse {
    private String token;
    
    public AuthResponse(String token) {
      this.token = token;
    }
    
    public String getToken() {
      return token;
    }
    
    public void setToken(String token) {
      this.token = token;
    }
  }
}
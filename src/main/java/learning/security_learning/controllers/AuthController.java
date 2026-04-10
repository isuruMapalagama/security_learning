package learning.security_learning.controllers;

import jakarta.validation.Valid;
import learning.security_learning.dto.request.LoginRequest;
import learning.security_learning.dto.request.RefreshTokenRequest;
import learning.security_learning.dto.request.RegisterRequest;
import learning.security_learning.dto.response.ApiResponse;
import learning.security_learning.dto.response.AuthResponse;
import learning.security_learning.dto.response.RegisterResponse;
import learning.security_learning.security.RsaKeyService;
import learning.security_learning.service.AuthService;
import learning.security_learning.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {


    private final UserService userService;
    private final AuthService authService;
    private final RsaKeyService rsaKeyService;

    @GetMapping("/public-key")
    public ResponseEntity<ApiResponse<String>> getPublicKey() {
        return ResponseEntity.ok(
                ApiResponse.success("Public key retrieved",
                        rsaKeyService.getPublicKeyBase64())
        );
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> registerUser(
            @Valid
            @RequestBody RegisterRequest request){

        RegisterResponse response = userService.registerUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);

        return ResponseEntity
                .ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @Valid
            @RequestBody RefreshTokenRequest request){

        AuthResponse response = authService.refresh(request);

        return ResponseEntity
                .ok(ApiResponse.success("Token refreshed successfully", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid
            @RequestBody RefreshTokenRequest request){

        authService.logout(request);

        return ResponseEntity
                .ok(ApiResponse.success("Logged out successfully", null));
    }
}

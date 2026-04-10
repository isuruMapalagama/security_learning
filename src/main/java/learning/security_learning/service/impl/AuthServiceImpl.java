package learning.security_learning.service.impl;

import jakarta.transaction.Transactional;
import learning.security_learning.dto.request.LoginRequest;
import learning.security_learning.dto.request.RefreshTokenRequest;
import learning.security_learning.dto.response.AuthResponse;
import learning.security_learning.exception.InvalidTokenException;
import learning.security_learning.model.RefreshToken;
import learning.security_learning.model.User;
import learning.security_learning.repositories.RefreshTokenRepository;
import learning.security_learning.repositories.UserRepository;
import learning.security_learning.security.JwtService;
import learning.security_learning.security.RsaKeyService;
import learning.security_learning.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RsaKeyService rsaKeyService;


    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {

        log.info("Login attempt for username: {}", request.getUsername());

        String decryptedPassword = rsaKeyService.decrypt(request.getPassword());
        log.info("Password decrypted successfully");

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        decryptedPassword
                )
        );
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new BadCredentialsException("Invalid username or password user"));
//
//        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
//            throw new BadCredentialsException("Invalid username or password");
//        }
        refreshTokenRepository.deleteAllByUser(user);

        String accessToken = jwtService
                .generateAccessToken(user.getUsername());
        String refreshToken = jwtService
                .generateRefreshToken(user.getUsername());

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .token(refreshToken)
                .user(user)
                .expiresAt(LocalDateTime.now()
                        .plusSeconds(refreshTokenExpiration / 1000))
                .build();

        refreshTokenRepository.save(refreshTokenEntity);
        log.info("User logged in successfully: {}", user.getUsername());

        return buildAuthResponse(accessToken, refreshToken, user);
    }

    @Override
    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {

        log.info("Refresh token request received");

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(request.getRefreshToken())
                .orElseThrow(() ->
                        new InvalidTokenException("Refresh token not found"));

        if (refreshToken.isRevoked()) {
            throw new InvalidTokenException("Refresh token has been revoked");
        }
        if(refreshToken.isExpired()){
            refreshTokenRepository.delete(refreshToken);
            throw new InvalidTokenException("Refresh token has expired. Please login again");
        }

        User user = refreshToken.getUser();
        String newAccessToken= jwtService
                .generateAccessToken(user.getUsername());

        log.info("Access token refreshed for user: {}", user.getUsername());

        return buildAuthResponse(newAccessToken, refreshToken.getToken(), user);
    }

    @Override
    @Transactional
    public void logout(RefreshTokenRequest request) {

        log.info("Logout request received");

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(request.getRefreshToken())
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found"));

        refreshTokenRepository.delete(refreshToken);

        log.info("User logged out successfully: {}", refreshToken.getUser().getUsername());
    }

    private AuthResponse buildAuthResponse(String accessToken, String refreshToken, User user){
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .email(user.getEmail())
                .tokenType("Bearer")
                .accessTokenExpiresIn(accessTokenExpiration / 1000)
                .refreshTokenExpiresIn(refreshTokenExpiration / 1000)
                .build();
    }
}




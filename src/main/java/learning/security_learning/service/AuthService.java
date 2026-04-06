package learning.security_learning.service;

import learning.security_learning.dto.request.LoginRequest;
import learning.security_learning.dto.request.RefreshTokenRequest;
import learning.security_learning.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse login(LoginRequest request);
    AuthResponse refresh(RefreshTokenRequest request);
    void logout(RefreshTokenRequest request);

}

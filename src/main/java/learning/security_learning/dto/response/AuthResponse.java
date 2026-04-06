package learning.security_learning.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String username;
    private String email;
    private String tokenType;
    private long accessTokenExpiresIn;
    private long refreshTokenExpiresIn;
}

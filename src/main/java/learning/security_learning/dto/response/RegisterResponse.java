package learning.security_learning.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
@Getter
@Builder
public class RegisterResponse {
    private Long id;
    private String username;
    private String email;
    private LocalDateTime createdAt;
}

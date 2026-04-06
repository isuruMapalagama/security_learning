package learning.security_learning.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class LoginRequest {

    @NotBlank(message = "Username is required !")
    private String username;

    @NotBlank(message = "Password is required !")
    private String password;
}

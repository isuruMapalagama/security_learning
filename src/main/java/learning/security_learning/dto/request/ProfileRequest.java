package learning.security_learning.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileRequest {

    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
}

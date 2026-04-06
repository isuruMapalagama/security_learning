package learning.security_learning.service;

import learning.security_learning.dto.request.RegisterRequest;
import learning.security_learning.dto.response.RegisterResponse;

public interface UserService {
    RegisterResponse registerUser(RegisterRequest request);
}

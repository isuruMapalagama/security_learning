package learning.security_learning.service.impl;

import jakarta.transaction.Transactional;
import learning.security_learning.client.ProfileClient;
import learning.security_learning.dto.request.ProfileRequest;
import learning.security_learning.dto.request.RegisterRequest;
import learning.security_learning.dto.response.RegisterResponse;
import learning.security_learning.exception.UserAlreadyExistsException;
import learning.security_learning.model.User;
import learning.security_learning.repositories.UserRepository;
import learning.security_learning.security.RsaKeyService;
import learning.security_learning.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ProfileClient profileClient;
    private final RsaKeyService rsaKeyService;


    @Override
    @Transactional
    public RegisterResponse registerUser(RegisterRequest request) {

        log.info("Registering user with email: {}", request.getEmail());

        if (userRepository.existsByUsername((request.getUsername()))) {
            throw new UserAlreadyExistsException(
                    "User with username " + request.getUsername() + " already exists");
        }
        if (userRepository.existsByEmail((request.getEmail()))) {
            throw new UserAlreadyExistsException(
                    "User with Email " + request.getEmail() + " already exists");
        }
        String decryptedPassword = rsaKeyService.decrypt(request.getPassword());
        log.info("Password decrypted successfully");

        String hashedPassword = passwordEncoder.encode(decryptedPassword);
        log.info("Password hashed successfully");

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(hashedPassword)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User Registered successfully. ID : {}", savedUser.getId());

        ProfileRequest profileRequest = ProfileRequest.builder()
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .build();

        profileClient.createProfile(profileRequest);

        return RegisterResponse.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .createdAt(savedUser.getCreatedAt())
                .build();
    }
}


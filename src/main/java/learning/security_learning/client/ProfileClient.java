package learning.security_learning.client;

import learning.security_learning.dto.request.ProfileRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfileClient {

    private final WebClient profileServiceClient;

    public void createProfile(ProfileRequest request){

        log.info("Calling profile_service for username: {}", request.getUsername());

        try{
            profileServiceClient
                    .post()
                    .uri("/api/profiles/create")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Profile created successfully for: {}", request.getUsername());

        } catch (Exception e){
            log.error("Failed to create profile for: {}. Error: {}",
                    request.getUsername(), e.getMessage());
        }
    }
}

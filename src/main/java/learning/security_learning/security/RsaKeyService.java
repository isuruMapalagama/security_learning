package learning.security_learning.security;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
@Component
@Getter
public class RsaKeyService {

    @Value("${rsa.public-key}")
    private String publicKeyBase64;

    @Value("${rsa.private-key}")
    private String privateKeyBase64;

    private PrivateKey privateKey;
    private PublicKey publicKey;


    @PostConstruct
    public void loadKeys() {
        try {
            log.info("Loading RSA keys...");

            byte[] privateKeyBytes = Base64.getDecoder()
                    .decode(privateKeyBase64.trim());
            PKCS8EncodedKeySpec privateKeySpec =
                    new PKCS8EncodedKeySpec(privateKeyBytes);
            this.privateKey = KeyFactory.getInstance("RSA")
                    .generatePrivate(privateKeySpec);

            byte[] publicKeyBytes = Base64.getDecoder()
                    .decode(publicKeyBase64.trim());
            X509EncodedKeySpec publicKeySpec =
                    new X509EncodedKeySpec(publicKeyBytes);
            this.publicKey = KeyFactory.getInstance("RSA")
                    .generatePublic(publicKeySpec);

            log.info("RSA keys loaded successfully!");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load RSA keys: " + e.getMessage(), e);
        }
    }

    public String decrypt(String encryptedBase64) {
        try {
            byte[] encryptedBytes = Base64.getDecoder()
                    .decode(encryptedBase64.trim());

            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            return new String(decryptedBytes);

        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt password", e);
        }
    }
}

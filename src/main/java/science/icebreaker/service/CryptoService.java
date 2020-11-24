package science.icebreaker.service;

import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.stream.Collector;

@Service
public class CryptoService {
    /**
     * Generates a cryptographically secure string of with a specified length
     * @param length
     * @return cryptographically secure string
     */
    public String getSecureSecret(int length) {
        final String tokens = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        return random.ints(length, 0, tokens.length())
        .mapToObj(val -> tokens.charAt(val))
        .collect(Collector.of(
            StringBuilder::new,
            StringBuilder::append,
            StringBuilder::append,
            StringBuilder::toString
        ));
    }
}

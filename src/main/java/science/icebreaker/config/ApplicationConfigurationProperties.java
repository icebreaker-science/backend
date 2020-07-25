package science.icebreaker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "icebreaker")
public class ApplicationConfigurationProperties {

    /**
     * This secret will be used to sign the JWT tokens.
     */
    private String jwtSecret;

    /**
     * The number of milliseconds for which a JWT token should be valid for.
     */
    private long jwtTokenValidityMs;


    public String getJwtSecret() {
        return jwtSecret;
    }


    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }


    public long getJwtTokenValidityMs() {
        return jwtTokenValidityMs;
    }


    public void setJwtTokenValidityMs(long jwtTokenValidityMs) {
        this.jwtTokenValidityMs = jwtTokenValidityMs;
    }
}

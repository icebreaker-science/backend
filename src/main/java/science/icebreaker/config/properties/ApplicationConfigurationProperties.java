package science.icebreaker.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "icebreaker")
public class ApplicationConfigurationProperties {

    /**
     * If the server is set to development mode,
     * it will allow cross-origin requests (CORS) and may return details
     * of exceptions.
     */
    private boolean development;


    /**
     * The host url of the website (frontend).
     */
    private String host;


    /**
     * This secret will be used to sign the JWT tokens.
     */
    private String jwtSecret;

    /**
     * The number of milliseconds for which a JWT token should be valid for.
     */
    private long jwtTokenValidityMs;


    public boolean getDevelopment() {
        return development;
    }


    public void setDevelopment(boolean development) {
        this.development = development;
    }


    public String getHost() {
        return host;
    }


    public void setHost(String host) {
        this.host = host;
    }


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

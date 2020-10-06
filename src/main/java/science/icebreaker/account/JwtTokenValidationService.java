package science.icebreaker.account;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class JwtTokenValidationService {

    private final String jwtSecret;


    public JwtTokenValidationService(
            @Value("${icebreaker.jwt-secret}") String jwtSecret
    ) {
        this.jwtSecret = jwtSecret;
    }


    /**
     * An exception will be thrown if the token is not valid. Please look up the documentation of
     * {@link io.jsonwebtoken.JwtParser#parseClaimsJws} to see the possible RuntimeExceptions.
     * @param jwtToken the jwt token
     * @return An account object; the password field is set to an empty string.
     */
    public Account validateJwtToken(String jwtToken) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(jwtToken)
                .getBody();
        return new Account((Integer) claims.get("account_id"), claims.getSubject(), "");
    }
}

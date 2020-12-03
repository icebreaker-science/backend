package science.icebreaker.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import science.icebreaker.dao.entity.Account;


@Service
public class JwtTokenValidationService {

    private final String jwtSecret;
    private final Set<String> invalidatedTokens;

    // The time of invalidation (all tokens issues before that are not accepted)
    private final Map<Integer, Date> accountTokensStartTime;

    public JwtTokenValidationService(
            @Value("${icebreaker.jwt-secret}") String jwtSecret
    ) {
        this.jwtSecret = jwtSecret;
        this.invalidatedTokens = new HashSet<String>();
        this.accountTokensStartTime = new HashMap<Integer, Date>();
    }

    /**
     * An exception will be thrown if the token is not valid. Please look up the documentation of
     * {@link io.jsonwebtoken.JwtParser#parseClaimsJws} to see the possible RuntimeExceptions.
     * @param jwtToken the jwt token
     * @return An account object; the password field is set to an empty string.
     */
    public Account validateJwtToken(String jwtToken) {
        if (this.invalidatedTokens.contains(jwtToken)) {
            throw new JwtException("Invalid Token");
        }

        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(jwtToken)
                .getBody();

        Integer accountId = (Integer) claims.get("account_id");

        Date startDate = this.accountTokensStartTime.get(accountId);
        if (startDate != null && claims.getIssuedAt().before(startDate)) {
            throw new JwtException("Invalid Token");
        }
        return new Account((Integer) claims.get("account_id"), claims.getSubject(), "");
    }

    /**
     * Invalidates the given token
     * @param token The token to invalidate
     */
    public void invalidateToken(String token) {
        this.invalidatedTokens.add(token);
    }

    /**
     * Invalidates all token already created for a specific user
     * @param accountId
     */
    public void invalidateUserTokens(Integer accountId) {
        this.accountTokensStartTime.put(accountId, new Date());
    }

}

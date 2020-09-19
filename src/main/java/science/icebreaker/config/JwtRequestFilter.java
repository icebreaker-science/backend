package science.icebreaker.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import science.icebreaker.account.Account;
import science.icebreaker.account.AccountRole;
import science.icebreaker.account.AccountRoleRepository;
import science.icebreaker.account.JwtTokenValidationService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


/**
 * Please refer to {@link SecurityConfig} for an overview of the security concept.
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtTokenValidationService jwtTokenValidationService;

    private final AccountRoleRepository accountRoleRepository;

    private final Integer tokenPrefixLength = 7; // 'Bearer '

    public JwtRequestFilter(
            JwtTokenValidationService jwtTokenValidationService,
            AccountRoleRepository accountRoleRepository
    ) {
        this.jwtTokenValidationService = jwtTokenValidationService;
        this.accountRoleRepository = accountRoleRepository;
    }


    /**
     * If the "Authorization" header is present,
     * this function will extract the JWT token and validate it. If the
     * validation is successful, the account and the roles of
     * the user will be loaded and added to the security context.
     * If not, it will throw exceptions (see {@link JwtTokenValidationService#validateJwtToken}.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String requestTokenHeader = request.getHeader("Authorization");
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            String jwtToken = requestTokenHeader.substring(this.tokenPrefixLength);
            Account account = jwtTokenValidationService.validateJwtToken(jwtToken);
            List<AccountRole> roles = accountRoleRepository.findAllByEmail(account.getEmail());

            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(account, null, roles);
            authenticationToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request, response);
    }
}

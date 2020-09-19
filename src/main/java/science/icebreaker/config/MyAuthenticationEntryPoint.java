package science.icebreaker.config;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * This implementation of {@link AuthenticationEntryPoint}
 * can be used to stop the framework's authentication
 * process. This class is used in {@link SecurityConfig#configure(HttpSecurity)}.
 * The only authentication process that should be allowed is through our
 * /account/login endpoint defined in
 * {@link science.icebreaker.account.AccountController#login(Account)}.
 */
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException)
    throws IOException, ServletException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
}

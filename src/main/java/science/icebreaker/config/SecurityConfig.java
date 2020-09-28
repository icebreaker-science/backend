package science.icebreaker.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import science.icebreaker.account.Account;
import science.icebreaker.account.RegistrationRequest;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;


/**
 * The security / authentication concept is as follows:
 *
 * Registration: To create a new account, /account/register ({@link science.icebreaker.account.AccountController#register(RegistrationRequest)}
 * has to be called. After validating the data, it will use the password encoder defined in this class to encode the password
 * and store the account information in to the database.
 *
 * Login: A login will be initiated through the /account/login endpoint ({@link science.icebreaker.account.AccountController#login(Account)}.
 * If the {@link AuthenticationManager} manager provided in this class verifies the login data, a JWT token will be
 * generated and sent back in the response.
 *
 * Authentication: Every request besides those explicitly specified in {@link #configure(HttpSecurity)} needs to be
 * authenticated. Every request goes through the {@link JwtRequestFilter} which looks for a JWT token in the
 * "Authorization" request header. If present, it will validate the JWT token and authorize the request if appropriate.
 * If a request is authorized, the user can later be accessed using {@link HttpServletRequest#getUserPrincipal()}.
 *
 * Please refer to the Spring Security documentation and {@link WebSecurityConfigurerAdapter} for further details.
 *
 * @see <a href="https://docs.spring.io/spring-security/site/docs/current/reference/html5/">Spring Security reference</a>
 * @see <a href="https://tools.ietf.org/html/rfc7519">JWT specification</a>
 * @see <a href="https://jwt.io/introduction/">JWT Introduction</a>
 */
@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthenticationEntryPoint entryPoint = new MyAuthenticationEntryPoint();

    private final DataSource dataSource;

    private final JwtRequestFilter jwtRequestFilter;


    @Autowired
    public SecurityConfig(
            DataSource dataSource,
            JwtRequestFilter jwtRequestFilter
    ) {
        this.dataSource = dataSource;
        this.jwtRequestFilter = jwtRequestFilter;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .cors().and()
            .authorizeRequests()
                .antMatchers(
                        "/v2/api-docs", "/webjars/**", "/swagger-resources/**", "/swagger-ui.html", // Swagger
                        "/", "/account/register", "/account/login", "/network/**"
                ).permitAll()
                .antMatchers(HttpMethod.GET, "/wiki", "/device-availability/").permitAll()
                .antMatchers(HttpMethod.POST, "/device-availability/{id:[0-9]+}/contact").permitAll()
            .anyRequest().authenticated().and()
            .exceptionHandling()
            // We don't want to start an authentication process at this point. See MyAuthenticationEntryPoint#commence
            .authenticationEntryPoint(entryPoint).and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(this.dataSource)
                .usersByUsernameQuery("select email as username, password, true from account where lower(email) = lower(?)")
                .authoritiesByUsernameQuery("select email as username, role as authority from account_role where lower(email) = lower(?)")
                .passwordEncoder(encoder());
    }


    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}

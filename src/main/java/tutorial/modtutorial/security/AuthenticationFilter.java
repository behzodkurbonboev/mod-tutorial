package tutorial.modtutorial.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import tutorial.modtutorial.domain.dto.TokenDTO;
import tutorial.modtutorial.domain.dto.user.request.UserDTO;
import tutorial.modtutorial.exception.Error;
import tutorial.modtutorial.utils.SecurityUtils;

import java.io.IOException;
import java.util.Set;


@Component
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtService jwtService;
    private final ObjectMapper mapper;
    private final Validator validator;

    public AuthenticationFilter(JwtService jwtService, Validator validator) {
        setFilterProcessesUrl("/api/v1/public/users/login");

        this.jwtService = jwtService;
        this.mapper = new ObjectMapper();
        this.validator = validator;
    }

    @Override
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        try {
            UserDTO user = mapper.readValue(request.getInputStream(), UserDTO.class);

            // manually validate userDTO
            Set<ConstraintViolation<UserDTO>> violations = validator.validate(user);
            if (!violations.isEmpty()) {
                throw new AuthenticationServiceException("Validation exception");
            }

            UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(user.getUsername(), user.getPassword());
            setDetails(request, authRequest);

            return getAuthenticationManager().authenticate(authRequest);
        } catch (IOException e) {
            throw new RuntimeException("Wrong data type is supplied");
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request, HttpServletResponse response,
            FilterChain chain, Authentication auth
    ) throws IOException {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();

        String access = jwtService.createAccessToken(principal);
        String refresh = jwtService.createRefreshToken(principal);
        Set<String> routes = SecurityUtils.toRoutes(principal.getAuthorities());

        TokenDTO token = new TokenDTO(principal.getUserId(), access, refresh, routes);
        String payload = mapper.writeValueAsString(token);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(payload);
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed
    ) throws IOException {
        Error error = new Error("login", "Telefon raqam yoki parol noto'g'ri");
        String payload = mapper.writeValueAsString(error);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(payload);
    }
}

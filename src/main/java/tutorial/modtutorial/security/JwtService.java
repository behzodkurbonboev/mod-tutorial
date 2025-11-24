package tutorial.modtutorial.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import tutorial.modtutorial.configuration.ApplicationProperties;

import java.util.Date;
import java.util.stream.Collectors;


@Component
public class JwtService {
    private final ApplicationProperties.Jwt props;

    public JwtService(ApplicationProperties applicationProperties) {
        this.props = applicationProperties.getJwt();
    }

    public String extractUserId(String token) {
        return extractAllClaims(token).getOrDefault(props.getUserClaim(), "").toString();
    }

    public String createAccessToken(UserPrincipal principal) {
        return generateToken(principal, props.getAccessTokenExpiration());
    }

    public String createRefreshToken(UserPrincipal principal) {
        return generateToken(principal, props.getRefreshTokenExpiration());
    }


    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(props.getPrivateKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(UserPrincipal principal, long expiration) {
        String authorities = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(principal.getUserId())
                .claim(props.getUserClaim(), principal.getUserId()) // be set as 'principal' when authorization
                .claim(props.getAuthClaim(), authorities)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(props.getPrivateKey(), SignatureAlgorithm.RS256).compact();
    }
}

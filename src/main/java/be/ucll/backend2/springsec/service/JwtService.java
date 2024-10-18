package be.ucll.backend2.springsec.service;

import be.ucll.backend2.springsec.config.JwtProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class JwtService {
    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;

    public JwtService(JwtEncoder jwtEncoder,
                      JwtProperties jwtProperties) {
        this.jwtEncoder = jwtEncoder;
        this.jwtProperties = jwtProperties;
    }

    public String createToken(long id, String emailAddress, List<String> scopes) {
        final var now = Instant.now();
        final var expiresAt = now.plus(jwtProperties.token().lifetime());
        final var claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.token().issuer())
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(Long.toString(id))
                .claim("email", emailAddress)
                .claim("scope", scopes)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String createToken(Authentication authentication) {
        final var user = ((UserDetailsImpl) authentication.getPrincipal()).user();
        final var scopes =
                authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return createToken(user.getId(), user.getEmailAddress(), scopes);
    }
}

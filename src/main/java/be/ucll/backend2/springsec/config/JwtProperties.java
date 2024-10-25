package be.ucll.backend2.springsec.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.boot.context.properties.bind.Name;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(@DefaultValue Keys keys,
                            @DefaultValue Token token) {
    public record Keys(@Name("public") RSAPublicKey publicKey,
                       @Name("private") RSAPrivateKey privateKey) {}
    public record Token(@DefaultValue("self") String issuer,
                        @DefaultValue("5m") Duration lifetime) {}
}

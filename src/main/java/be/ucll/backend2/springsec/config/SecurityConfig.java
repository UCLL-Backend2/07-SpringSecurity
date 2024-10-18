package be.ucll.backend2.springsec.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.h2.H2ConsoleProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    @Order(0)
    @ConditionalOnBean(H2ConsoleProperties.class)
    public SecurityFilterChain h2SecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher(toH2Console())
                .csrf(AbstractHttpConfigurer::disable)
                // Geen HTTP basic nodig (H2 doet sec zelf!)
                .headers(headers -> headers.frameOptions(frameOptionsConfig -> frameOptionsConfig.disable()))
                .authorizeHttpRequests(
                        authorizeRequests -> authorizeRequests.anyRequest().permitAll()
                )
                .build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(
                        authorizeRequests ->
                                authorizeRequests
                                        .requestMatchers("/api/v1/auth/**").permitAll()
                                        .anyRequest().authenticated()
                )
                .build();
    }
}

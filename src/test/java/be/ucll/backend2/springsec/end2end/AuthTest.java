package be.ucll.backend2.springsec.end2end;

import be.ucll.backend2.springsec.controller.dto.LoginDto;
import be.ucll.backend2.springsec.controller.dto.RegisterUserDto;
import be.ucll.backend2.springsec.controller.dto.TokenDto;
import be.ucll.backend2.springsec.entity.Role;
import be.ucll.backend2.springsec.entity.User;
import be.ucll.backend2.springsec.repository.UserRepository;
import be.ucll.backend2.springsec.service.JwtService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Sql("classpath:schema.sql")
@Sql("classpath:data.sql")
public class AuthTest {
    @Autowired
    private WebTestClient client;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Qualifier("jwtDecoder")
    @Autowired
    private JwtDecoder jwtDecoder;

    @Test
    public void givenUserIsNotRegistered_whenPostRegister_thenUserIsRegistered() {
        final var userToRegister = new RegisterUserDto(
                "clement@example.com", "password", "Clement", "Peerens"
        );

        client.post()
                .uri("/api/v1/auth/register")
                .bodyValue(userToRegister)
                .exchange()
                .expectStatus().isCreated()
                .expectBody().json("""
                                   {
                                     "id": 2,
                                     "emailAddress": "clement@example.com",
                                     "firstName": "Clement",
                                     "lastName": "Peerens",
                                     "role": "User"
                                   }
                                   """,
                true);

        final var user = userRepository.findById(2L).orElseThrow(() -> new AssertionFailedError("User not found"));
        Assertions.assertEquals(2L, user.getId());
        Assertions.assertEquals("clement@example.com", user.getEmailAddress());
        Assertions.assertTrue(passwordEncoder.matches("password", user.getPassword()));
        Assertions.assertEquals("Clement", user.getFirstName());
        Assertions.assertEquals("Peerens", user.getLastName());
        Assertions.assertEquals(Role.User, user.getRole());
    }

    @Test
    public void givenAdminIsRegistered_whenPostLogin_thenTokenIsCreated() {
        final var loginInfo = new LoginDto("jos@example.com", "password");

        final var result = client.post()
                .uri("/api/v1/auth/login")
                .bodyValue(loginInfo)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TokenDto.class)
                .returnResult().getResponseBody();

        final var token = jwtDecoder.decode(result.token());

        Assertions.assertEquals("1", token.getSubject());
        Assertions.assertEquals("jos@example.com", token.getClaimAsString("email"));
        Assertions.assertEquals(List.of("ROLE_ADMIN", "ROLE_USER"), token.getClaimAsStringList("scope"));
    }

    @Test
    public void givenUserIsRegisterd_whenPostLogin_thenTokenIsCreated() {
        final var user = new User("clement@example.com", "{noop}password", "Clement", "Peerens", Role.User);
        userRepository.save(user);
        final var loginInfo = new LoginDto("clement@example.com", "password");

        final var result = client.post()
                .uri("/api/v1/auth/login")
                .bodyValue(loginInfo)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TokenDto.class)
                .returnResult().getResponseBody();

        final var token = jwtDecoder.decode(result.token());

        Assertions.assertEquals("2", token.getSubject());
        Assertions.assertEquals("clement@example.com", token.getClaimAsString("email"));
        Assertions.assertEquals(List.of("ROLE_USER"), token.getClaimAsStringList("scope"));
    }
}

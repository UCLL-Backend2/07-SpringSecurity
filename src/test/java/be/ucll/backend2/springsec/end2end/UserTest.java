package be.ucll.backend2.springsec.end2end;

import be.ucll.backend2.springsec.entity.Role;
import be.ucll.backend2.springsec.entity.User;
import be.ucll.backend2.springsec.repository.UserRepository;
import be.ucll.backend2.springsec.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Sql("classpath:schema.sql")
@Sql("classpath:data.sql")
public class UserTest {
    @Autowired
    private WebTestClient client;

    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void createNormalUser() {
        final var clement = new User(
                "clement@example.com",
                "{noop}passwod",
                "Clement",
                "Peerens",
                Role.User);
        userRepository.save(clement);
    }

    private String adminToken() {
        return jwtService.createToken(1L, "jos@example.com", List.of("ROLE_ADMIN", "ROLE_USER"));
    }

    private String userToken() {
        return jwtService.createToken(2L, "clement@example.com", List.of("ROLE_USER"));
    }

    @Test
    public void givenAdminIsAuthenticated_whenGetUser_thenUsersAreReturned() {
        client.get()
                .uri("/api/v1/user")
                .header("Authorization", "Bearer " + adminToken())
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("""
                                   [
                                     {
                                       "id": 1,
                                       "emailAddress": "jos@example.com",
                                       "firstName": "Jos",
                                       "lastName": "Bosmans",
                                       "role": "Admin"
                                     },
                                     {
                                       "id": 2,
                                       "emailAddress": "clement@example.com",
                                       "firstName": "Clement",
                                       "lastName": "Peerens",
                                       "role": "User"
                                     }
                                   ]
                                   """,
                true);
    }

    @Test
    public void givenUserIsAuthenticated_whenPutUser_thenUserIsUpdatedExceptRole() {
        client.put()
                .uri("/api/v1/user/2")
                .header("Authorization", "Bearer " + userToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                           {
                             "emailAddress": "jim@example.com",
                             "firstName": "Jim",
                             "lastName": "Halpert",
                             "role": "Admin"
                           }
                           """)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("""
                                   {
                                     "id": 2,
                                     "emailAddress": "jim@example.com",
                                     "firstName": "Jim",
                                     "lastName": "Halpert",
                                     "role": "User"
                                   }
                                   """,
                        true);
    }

    @Test
    public void givenAdminIsAuthenticated_whenPutUser_thenUserIsUpdated() {
        client.put()
                .uri("/api/v1/user/2")
                .header("Authorization", "Bearer " + adminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                           {
                             "emailAddress": "jim@example.com",
                             "firstName": "Jim",
                             "lastName": "Halpert",
                             "role": "Admin"
                           }
                           """)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("""
                                   {
                                     "id": 2,
                                     "emailAddress": "jim@example.com",
                                     "firstName": "Jim",
                                     "lastName": "Halpert",
                                     "role": "Admin"
                                   }
                                   """,
                        true);
    }
}

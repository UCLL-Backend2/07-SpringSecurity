package be.ucll.backend2.springsec.end2end;

import be.ucll.backend2.springsec.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class HelloTest {
    @Autowired
    private WebTestClient client;

    @Autowired
    private JwtService jwtService;

    @Test
    public void whenGetHelloIsRequested_thenHelloMessageIsReturned() {
        final var token = jwtService.createToken(1L, "jos@example.com", List.of("ROLE_USER"));

        client.get()
                .uri("/api/v1/hello")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("""
                                   {
                                     "message": "Hello, 1"
                                   }
                                   """);
    }
}

package be.ucll.backend2.springsec.end2end;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class HelloTest {
    @Autowired
    private WebTestClient client;

    @Test
    public void whenGetHelloIsRequested_thenHelloMessageIsReturned() {
        client.get()
                .uri("/api/v1/hello")
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("""
                                   {
                                     "message": "Hello"
                                   }
                                   """);
    }
}

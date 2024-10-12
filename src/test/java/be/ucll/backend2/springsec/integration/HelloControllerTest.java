package be.ucll.backend2.springsec.integration;

import be.ucll.backend2.springsec.controller.HelloController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebMvcTest(HelloController.class)
public class HelloControllerTest {
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

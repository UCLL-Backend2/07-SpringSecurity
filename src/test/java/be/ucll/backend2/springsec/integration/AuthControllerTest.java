package be.ucll.backend2.springsec.integration;

import be.ucll.backend2.springsec.config.SecurityConfig;
import be.ucll.backend2.springsec.controller.AuthController;
import be.ucll.backend2.springsec.controller.dto.LoginDto;
import be.ucll.backend2.springsec.controller.dto.RegisterUserDto;
import be.ucll.backend2.springsec.entity.Role;
import be.ucll.backend2.springsec.entity.User;
import be.ucll.backend2.springsec.service.AuthService;
import be.ucll.backend2.springsec.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {
    @Autowired
    private WebTestClient client;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserService userService;

    @Test
    public void whenRegisterUser_thenUserIsAdded() {
        final var userToRegister = new RegisterUserDto(
                "jos@example.com",
                "password",
                "Jos",
                "Bosmans"
        );
        final var userToReturn = new User(
                "jos@example.com",
                "{noop}password",
                "Jos",
                "Bosmans",
                Role.User
        );
        userToReturn.setId(1L);

        Mockito.when(userService.addUser(userToRegister)).thenReturn(userToReturn);

        client.post()
                .uri("/api/v1/auth/register")
                .bodyValue(new RegisterUserDto(
                        "jos@example.com",
                        "password",
                        "Jos",
                        "Bosmans"
                ))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .json("""
                      {
                        "id": 1,
                        "emailAddress": "jos@example.com",
                        "firstName": "Jos",
                        "lastName": "Bosmans",
                        "role": "User"
                      }
                      """,
                        true);

        Mockito.verify(userService).addUser(userToRegister);
    }

    @Test
    public void givenUserIsRegistered_whenPostLogin_thenTokenIsReturned() {
        final var token = "TOKEN";

        Mockito.when(authService.login(Mockito.anyString(), Mockito.anyString())).thenReturn(token);

        client.post()
                .uri("/api/v1/auth/login")
                .bodyValue(new LoginDto("jos@example.com", "password"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .json("""
                      {
                        "token": "TOKEN"
                      }
                      """,
                        true);

        Mockito.verify(authService).login("jos@example.com", "password");
    }
}

package be.ucll.backend2.springsec.integration;

import be.ucll.backend2.springsec.config.SecurityConfig;
import be.ucll.backend2.springsec.controller.UserController;
import be.ucll.backend2.springsec.entity.Role;
import be.ucll.backend2.springsec.entity.User;
import be.ucll.backend2.springsec.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
public class UserControllerTest {
    @Autowired
    private WebTestClient client;

    @MockBean
    private UserService userService;

    @Test
    public void givenUserIsNotAuthenticated_whenGetUsersIsRequested_then401IsReturned() {
        client.get()
                .uri("/api/v1/user")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @WithMockUser(username = "jos@example.com", roles = {"USER"})
    public void givenUserIsAuthenticated_whenGetUsersIsRequested_then403IsReturned() {
        client.get()
                .uri("/api/v1/user")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @WithMockUser(username = "jos@example.com", roles = {"ADMIN", "USER"})
    public void givenAdminIsAuthenticated_whenGetUsersIsRequested_thenUsersAreReturned() {
        Mockito.when(userService.getAllUsers()).thenReturn(List.of(
                new User(
                        "jos@example.com",
                        "{noop}password",
                        "Jos",
                        "Bosmans",
                        Role.Admin
                )
        ));

        client.get()
                .uri("/api/v1/user")
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("""
                                   [
                                     {
                                       "emailAddress": "jos@example.com",
                                       "firstName": "Jos",
                                       "lastName": "Bosmans",
                                       "role": "Admin"
                                     }
                                   ]
                                   """);
    }

    @Test
    @WithMockUser(value = "1", roles = {"USER"})
    public void givenUserIsAuthenticated_whenPutUser_thenUserIsUpdated() {
        final var userToReturn = new User(
                "jim@example.com",
                "{noop}password",
                "Jim",
                "Halpert",
                Role.User
        );
        userToReturn.setId(1L);
        Mockito.when(userService.updateUser(Mockito.any(), Mockito.anyLong(), Mockito.any())).thenReturn(userToReturn);

        client.put()
                .uri("/api/v1/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                           {
                             "emailAddress": "jim@example.com",
                             "firstName": "Jim",
                             "lastName": "Halpert"
                           }
                           """)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("""
                                   {
                                     "id": 1,
                                     "emailAddress": "jim@example.com",
                                     "firstName": "Jim",
                                     "lastName": "Halpert",
                                     "role": "User"
                                   }
                                   """,
                                   true);

        Mockito.verify(userService).updateUser(Mockito.any(), Mockito.eq(1L), Mockito.any());
    }

    @Test
    @WithMockUser(value = "1", roles = {"ADMIN", "USER"})
    public void givenAdminIsAuthenticated_whenPutUser_thenUserIsUpdated() {
        final var userToReturn = new User(
                "jim@example.com",
                "{noop}password",
                "Jim",
                "Halpert",
                Role.User
        );
        userToReturn.setId(2L);
        Mockito.when(userService.updateUser(Mockito.any(), Mockito.anyLong(), Mockito.any())).thenReturn(userToReturn);

        client.put()
                .uri("/api/v1/user/2")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                           {
                             "emailAddress": "jim@example.com",
                             "firstName": "Jim",
                             "lastName": "Halpert"
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

        Mockito.verify(userService).updateUser(Mockito.any(), Mockito.eq(2L), Mockito.any());
    }

    @Test
    @WithMockUser(value = "1", roles = "USER")
    public void givenUser1IsAuthenticated_whenPutUser2_then403IsReturned() {
        client.put()
                .uri("/api/v1/user/2")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                           {
                             "emailAddress": "jim@example.com",
                             "firstName": "Jim",
                             "lastName": "Halpert"
                           }
                           """)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    public void givenNoUserAuthenticated_whenPutUser_then401IsReturned() {
        client.put()
                .uri("/api/v1/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                           {
                             "emailAddress": "jim@example.com",
                             "firstName": "Jim",
                             "lastName": "Halpert"
                           }
                           """)
                .exchange()
                .expectStatus().isUnauthorized();
    }
}

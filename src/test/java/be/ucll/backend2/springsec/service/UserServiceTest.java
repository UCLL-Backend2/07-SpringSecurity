package be.ucll.backend2.springsec.service;

import be.ucll.backend2.springsec.entity.Role;
import be.ucll.backend2.springsec.entity.User;
import be.ucll.backend2.springsec.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void givenUserIsRegistered_whenUpdateUser_thenUserIsUpdated() {
        final var userInDb = new User(
                "jos@example.com",
                "{noop}password",
                "Jos",
                "Bosmans",
                Role.User
        );
        userInDb.setId(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(userInDb));
        // Just return same user as we got as the argument
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final var authentication = Mockito.mock(Authentication.class);
        Mockito.doReturn(AuthorityUtils.createAuthorityList("ROLE_USER")).when(authentication).getAuthorities();

        final var update = new User(
                "clement@example.com",
                "{noop}password",
                "Clement",
                "Peerens",
                Role.Admin
        );

        final var updatedUser = userService.updateUser(authentication, 1L, update);

        Assertions.assertEquals(1L, updatedUser.getId());
        Assertions.assertEquals("clement@example.com", updatedUser.getEmailAddress());
        Assertions.assertEquals("Clement", updatedUser.getFirstName());
        Assertions.assertEquals("Peerens", updatedUser.getLastName());
        // Role is unchanged (user is not admin)
        Assertions.assertEquals(Role.User, updatedUser.getRole());
    }

    @Test
    public void givenUserIsRegistered_whenPartialUpdateUser_thenUserIsUpdated() {
        final var userInDb = new User(
                "jos@example.com",
                "{noop}password",
                "Jos",
                "Bosmans",
                Role.User
        );
        userInDb.setId(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(userInDb));
        // Just return same user as we got as the argument
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final var authentication = Mockito.mock(Authentication.class);
        Mockito.doReturn(AuthorityUtils.createAuthorityList("ROLE_USER")).when(authentication).getAuthorities();

        final var update = new User(
                "jos2@example.com",
                null,
                null,
                null,
                null
        );

        final var updatedUser = userService.updateUser(authentication, 1L, update);

        Assertions.assertEquals(1L, updatedUser.getId());
        Assertions.assertEquals("jos2@example.com", updatedUser.getEmailAddress());
        Assertions.assertEquals("Jos", updatedUser.getFirstName());
        Assertions.assertEquals("Bosmans", updatedUser.getLastName());
        Assertions.assertEquals(Role.User, updatedUser.getRole());
    }

    @Test
    public void givenUserIsRegistered_whenUpdateUserAsAdmin_thenUserIsUpdated() {
        final var userInDb = new User(
                "jos@example.com",
                "{noop}password",
                "Jos",
                "Bosmans",
                Role.User
        );
        userInDb.setId(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(userInDb));
        // Just return same user as we got as the argument
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final var authentication = Mockito.mock(Authentication.class);
        Mockito.doReturn(AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER")).when(authentication).getAuthorities();

        final var update = new User(
                "clement@example.com",
                "{noop}password",
                "Clement",
                "Peerens",
                Role.Admin
        );

        final var updatedUser = userService.updateUser(authentication, 1L, update);

        Assertions.assertEquals(1L, updatedUser.getId());
        Assertions.assertEquals("clement@example.com", updatedUser.getEmailAddress());
        Assertions.assertEquals("Clement", updatedUser.getFirstName());
        Assertions.assertEquals("Peerens", updatedUser.getLastName());
        // Role is changed (user is admin)
        Assertions.assertEquals(Role.Admin, updatedUser.getRole());
    }
}

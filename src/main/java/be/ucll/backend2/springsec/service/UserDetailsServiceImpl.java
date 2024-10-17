package be.ucll.backend2.springsec.service;

import be.ucll.backend2.springsec.entity.Role;
import be.ucll.backend2.springsec.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final var user = userRepository
                .findByEmailAddress(username.toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        List<String> roles;
        if (user.getRole() == Role.Admin) {
            roles = List.of("ROLE_ADMIN", "ROLE_USER");
        } else {
            roles = List.of("ROLE_USER");
        }
        return User
                .withUsername(user.getEmailAddress())
                .password(user.getPassword())
                .authorities(roles.toArray(new String[0]))
                .build();
    }
}

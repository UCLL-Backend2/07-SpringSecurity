package be.ucll.backend2.springsec.service;

import be.ucll.backend2.springsec.controller.dto.RegisterUserDto;
import be.ucll.backend2.springsec.entity.Role;
import be.ucll.backend2.springsec.entity.User;
import be.ucll.backend2.springsec.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User addUser(RegisterUserDto registerUserDto) {
        final var hashedPassword = passwordEncoder.encode(registerUserDto.password());
        final var user = new User(
                registerUserDto.emailAddress(),
                hashedPassword,
                registerUserDto.firstName(),
                registerUserDto.lastName(),
                Role.User
        );
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(long id, User userUpdate) {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        final var isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        final var user = userRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("User with id " + id + " not found"));
        if (!isAdmin && !user.getEmailAddress().equals(authentication.getName())) {
            throw new AccessDeniedException("Not allowed to change user with id " + id);
        }
        if (userUpdate.getEmailAddress() != null) {
            user.setEmailAddress(userUpdate.getEmailAddress());
        }
        if (userUpdate.getFirstName() != null) {
            user.setFirstName(userUpdate.getFirstName());
        }
        if (userUpdate.getLastName() != null) {
            user.setLastName(userUpdate.getLastName());
        }
        if (isAdmin && userUpdate.getRole() != null) {
            user.setRole(userUpdate.getRole());
        }
        return userRepository.save(user);
    }
}

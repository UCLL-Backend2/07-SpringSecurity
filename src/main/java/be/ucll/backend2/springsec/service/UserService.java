package be.ucll.backend2.springsec.service;

import be.ucll.backend2.springsec.controller.dto.RegisterUserDto;
import be.ucll.backend2.springsec.entity.User;
import be.ucll.backend2.springsec.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
                registerUserDto.lastName()
        );
        return userRepository.save(user);
    }
}

package be.ucll.backend2.springsec.service;

import be.ucll.backend2.springsec.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsPasswordServiceImpl implements UserDetailsPasswordService {
    private final UserRepository userRepository;

    public UserDetailsPasswordServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails updatePassword(UserDetails userDetails, String newPassword) {
        final var user = ((UserDetailsImpl) userDetails).user();
        user.setPassword(newPassword);
        userRepository.save(user);
        return userDetails;
    }
}

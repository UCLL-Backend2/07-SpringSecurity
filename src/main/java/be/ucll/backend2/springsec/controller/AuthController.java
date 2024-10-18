package be.ucll.backend2.springsec.controller;

import be.ucll.backend2.springsec.controller.dto.LoginDto;
import be.ucll.backend2.springsec.controller.dto.RegisterUserDto;
import be.ucll.backend2.springsec.controller.dto.TokenDto;
import be.ucll.backend2.springsec.entity.User;
import be.ucll.backend2.springsec.service.AuthService;
import be.ucll.backend2.springsec.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User registerUser(@RequestBody RegisterUserDto registerUserDto) {
        return userService.addUser(registerUserDto);
    }

    @PostMapping("/login")
    public TokenDto login(@RequestBody LoginDto loginDto) {
        return new TokenDto(authService.login(loginDto.emailAddress(), loginDto.password()));
    }
}

package be.ucll.backend2.springsec.controller;

import be.ucll.backend2.springsec.controller.dto.RegisterUserDto;
import be.ucll.backend2.springsec.entity.User;
import be.ucll.backend2.springsec.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User registerUser(@RequestBody RegisterUserDto registerUserDto) {
        return userService.addUser(registerUserDto);
    }
}

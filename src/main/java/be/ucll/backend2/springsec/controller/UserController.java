package be.ucll.backend2.springsec.controller;

import be.ucll.backend2.springsec.entity.User;
import be.ucll.backend2.springsec.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') || principal.username == #id")
    public User updateUser(@PathVariable long id,
                           @RequestBody User user) {
        return userService.updateUser(id, user);
    }
}

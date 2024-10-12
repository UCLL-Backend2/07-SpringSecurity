package be.ucll.backend2.springsec.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hello")
public class HelloController {
    public record HelloDto(String message) {
    }

    @GetMapping
    public HelloDto hello(Authentication authentication) {
        return new HelloDto("Hello, " + authentication.getName());
    }
}

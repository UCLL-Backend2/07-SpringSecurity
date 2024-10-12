package be.ucll.backend2.springsec.controller.dto;

public record RegisterUserDto(String emailAddress, String password, String firstName, String lastName) {
}

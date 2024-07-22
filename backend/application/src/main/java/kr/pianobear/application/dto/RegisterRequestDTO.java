package kr.pianobear.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequestDTO {

    @NotBlank
    @Size(min = 2, max = 20)
    private String id;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String name;

    @NotBlank
    private Character gender;

    @NotBlank
    private LocalDate birthday;

    @NotBlank
    @Size(min = 2, max = 100)
    private String password;

    private String profilePic;
    private String statusMessage;
}

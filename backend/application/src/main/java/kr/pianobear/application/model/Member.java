package kr.pianobear.application.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class Member {

    @Id
    private String id;
    private String email;
    private String name;
    private Character gender;
    private LocalDate birthday;
    private String password;
    private String profilePic;
    private String statusMessage;
    private Boolean authEmail;
    private String role;
}

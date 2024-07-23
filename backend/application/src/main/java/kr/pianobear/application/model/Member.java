package kr.pianobear.application.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
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

    @OneToOne(targetEntity = FileData.class, optional = true)
    private FileData profilePic;

    private String statusMessage;
    private Boolean authEmail;
    private String role;
}

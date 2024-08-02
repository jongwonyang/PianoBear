package kr.pianobear.application.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@Entity
public class Member {

    @Id
    private String id;

    private String email;
    private String name;
    private char gender;
    private LocalDate birthday;
    private String password;

    @OneToOne(targetEntity = FileData.class)
    @Nullable
    private FileData profilePic;

    private String statusMessage;
    private boolean authEmail;
    private String role;

    @ManyToMany
    @JoinTable
    private Set<Member> friends;
}

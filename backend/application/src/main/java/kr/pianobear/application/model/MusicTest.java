package kr.pianobear.application.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "music_test")
public class MusicTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int grade;
    private String userId;
    private int musicId;
}

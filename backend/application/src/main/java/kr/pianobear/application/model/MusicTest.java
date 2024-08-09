package kr.pianobear.application.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class MusicTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int grade;
    private LocalDate testDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "music_id")
    private Music music;
}

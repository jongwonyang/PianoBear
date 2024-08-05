package kr.pianobear.application.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "music_practice")
public class MusicPractice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalDate practiceDate;
    private int practiceCount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "music_id")
    private Music music;

    public int getMusicId() {
        return music.getId();
    }

    public String getUserId() {
        return member.getId();
    }
}

package kr.pianobear.application.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Nullable
public class MusicHighScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "music_id", nullable = false)
    private Music music;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Member member;

    private int score;
}

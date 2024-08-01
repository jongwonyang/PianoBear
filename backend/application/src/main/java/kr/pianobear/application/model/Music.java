package kr.pianobear.application.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "music")
public class Music {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;
    private String originalFileRoute;
    private String changedFileRoute;
    private int practiceCount;
    private String recentPractice;
    private String userId;
    private String musicImg;
    private Boolean favorite;
    private LocalDateTime uploadDate;
    private String artist;
    private int highestScore;
}

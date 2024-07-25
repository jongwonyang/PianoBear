package kr.pianobear.application.model;

import jakarta.persistence.*;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "music_practice")
public class MusicPractice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Date practiceDate;
    private int practiceCount;
    private int musicId;
    private String userId;

}

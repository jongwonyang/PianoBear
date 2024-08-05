package kr.pianobear.application.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Data
public class Music {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;
    private String originalFileRoute;
    private String musicXmlRoute;
    private String modifiedMusicXmlRoute;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private Member user; // 외래 키로 설정하여 기본 제공 악보의 경우 null 가능

    private String musicImg;
    private Boolean favorite;
    private LocalDate uploadDate;
    private String artist;

    @OneToMany(mappedBy = "music", cascade = CascadeType.ALL)
    private List<MusicPractice> practices;

    @OneToMany(mappedBy = "music", cascade = CascadeType.ALL)
    private List<MusicTest> tests;

    public void setUser(Member user) {
        this.user = user;
    }

    public Member getUser() {
        return user;
    }

    public Music(int id) {
        this.id = id;
    }

    public Music() {
    }
}

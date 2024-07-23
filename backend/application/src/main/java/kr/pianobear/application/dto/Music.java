package kr.pianobear.application.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class Music {
    private int id;
    private String title;
    private String originalFileRoute;
    private String changedFileRoute;
    private int practiceCount;
    private String recentPractice;
    private String userId;
    private String musicImg;
    private Boolean favorite;
    private String uploadDate;
}

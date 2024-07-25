package kr.pianobear.application.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class MusicPractice {
    private int id;
    private String practiceDate;
    private int practiceCount;
    private int musicId;
    private String userId;
}

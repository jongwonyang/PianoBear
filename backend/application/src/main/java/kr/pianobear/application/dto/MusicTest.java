package kr.pianobear.application.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class MusicTest {
    private int id;
    private int grade;
    private String userId;
    private int musicId;
}

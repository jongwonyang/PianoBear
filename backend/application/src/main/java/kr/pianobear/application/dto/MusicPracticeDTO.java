package kr.pianobear.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import kr.pianobear.application.model.MusicPractice;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
@Schema(description = "연주 연습 DTO")
public class MusicPracticeDTO {

    @NotBlank
    @Schema(description = "연습 아이디")
    private int id;

    @NotBlank
    @Schema(description = "연습 날짜")
    private LocalDate practiceDate;

    private String formattedPracticeDate;

    @NotBlank
    @Schema(description = "날짜별 연습량")
    private int practiceCount; //겹치는 날짜는 하나씩 증가하게 하기

    @NotBlank
    @Schema(description = "악보 아이디")
    private int musicId;

    @Schema(description = "악보 제목")
    private String musicTitle;

    @NotBlank
    @Schema(description = "사용자 아이디")
    private String userId;

    public static MusicPracticeDTO fromMusicPractice(MusicPractice musicPractice) {
        MusicPracticeDTO musicPracticeDTO = new MusicPracticeDTO();
        musicPracticeDTO.setId(musicPractice.getId());
        musicPracticeDTO.setUserId(musicPractice.getUserId());
        musicPracticeDTO.setMusicId(musicPractice.getMusic().getId());
        musicPracticeDTO.setMusicTitle(musicPractice.getMusic().getTitle());
        musicPracticeDTO.setPracticeDate(LocalDate.parse(musicPractice.getPracticeDate().toString()));
        musicPracticeDTO.setPracticeCount(musicPractice.getPracticeCount());
        return musicPracticeDTO;
    }

    public String getFormattedPracticeDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return this.practiceDate.format(formatter);
    }
}

package kr.pianobear.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import kr.pianobear.application.model.FileData;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Schema(description = "프로필 사진 수정 DTO")
public class PhotoUpdateRequestDTO {

    public PhotoUpdateRequestDTO(){}

    public PhotoUpdateRequestDTO(String id, FileData profilePic){
        this.id = id;
        this.profilePic = profilePic;
    }

    @NotBlank
    @Schema(description = "사용자 아이디")
    private String id;

    @NotBlank
    @Schema(description = "새 프로필 사진")
    private FileData profilePic;
}

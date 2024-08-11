package kr.pianobear.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Schema(description = "소통방 - 방생성 요청 DTO")
public class CommunitySessionCreationDTO {
    @NotBlank
    @Schema(description = "방 제목")
    private String sessionTitle;

    @Schema(description = "초대 메시지")
    private String invitationMessage;

    @Schema(description = "설명")
    private String description;

    
}
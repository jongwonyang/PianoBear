package kr.pianobear.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Schema(description = "이름 수정 DTO")
public class NameUpdateRequestDTO {

    public NameUpdateRequestDTO() {}

    public NameUpdateRequestDTO(String id, String newName){
        this.id = id;
        this.newName = newName;
    }

    @NotBlank
    @Schema(description = "사용자 아이디")
    private String id;

    @NotBlank
    @Schema(description = "새로운 이름")
    private String newName;
}

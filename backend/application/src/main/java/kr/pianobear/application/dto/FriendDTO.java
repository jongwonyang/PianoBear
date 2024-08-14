package kr.pianobear.application.dto;

import jakarta.annotation.Nullable;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import kr.pianobear.application.model.FileData;
import kr.pianobear.application.model.Member;
import kr.pianobear.application.service.FileDataService;
import lombok.Data;

@Data
public class FriendDTO {

    @Id
    private String id;

    private String name;

    private String profilePic;

    private String statusMessage;

    public static FriendDTO fromMember(Member member) {
        FriendDTO friendDTO = new FriendDTO();
        friendDTO.setId(member.getId());
        friendDTO.setName(member.getName());

        String downloadPath = FileDataService.getDownloadPath(member.getProfilePic()).orElse("/api/v1/static/pitch-head.png");
        friendDTO.setProfilePic(downloadPath);
        friendDTO.setStatusMessage(member.getStatusMessage());
        return friendDTO;
    }
}

package kr.pianobear.application.dto;

import jakarta.annotation.Nullable;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import kr.pianobear.application.model.FileData;
import kr.pianobear.application.model.Member;
import lombok.Data;

@Data
public class FriendDTO {

    @Id
    private String id;

    private String name;

    @OneToOne(targetEntity = FileData.class)
    @Nullable
    private FileData profilePic;

    private String statusMessage;

    public static FriendDTO fromMember(Member member) {
        FriendDTO friendDTO = new FriendDTO();
        friendDTO.setId(member.getId());
        friendDTO.setName(member.getName());
        friendDTO.setProfilePic(member.getProfilePic());
        friendDTO.setStatusMessage(member.getStatusMessage());
        return friendDTO;
    }
}

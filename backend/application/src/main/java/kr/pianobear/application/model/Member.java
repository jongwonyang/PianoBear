package kr.pianobear.application.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@Entity
@EqualsAndHashCode(exclude = "friends")
@ToString(exclude = "friends")
public class Member {

    @Id
    private String id;

    private String email;
    private String name;
    private char gender;
    private LocalDate birthday;
    private String password;

    @OneToOne(targetEntity = FileData.class)
    @Nullable
    private FileData profilePic;

    private String statusMessage;
    private boolean authEmail;
    private String role;

    public Member() {
    }

    public Member(String id) {
        this.id = id;
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Music> musicList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MusicPractice> practices;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MusicTest> tests;

    @ManyToMany
    @JoinTable
    private Set<Member> friends;

    public void addFriend(Member friend) {
        this.friends.add(friend);
        friend.getFriends().add(this);
    }

    public void removeFriend(Member friend) {
        this.friends.remove(friend);
        friend.getFriends().remove(this);
    }

}

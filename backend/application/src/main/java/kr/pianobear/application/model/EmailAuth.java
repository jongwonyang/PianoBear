package kr.pianobear.application.model;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash("EmailAuth")
@Data
public class EmailAuth implements Serializable {

    @Id
    private String uuid;

    private String memberId;

    @Email
    private String emailAddress;

    private boolean verified;
}

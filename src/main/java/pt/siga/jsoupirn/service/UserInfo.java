package pt.siga.jsoupirn.service;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
@RedisHash("users")
public class UserInfo implements Serializable {
    @Id
    private final Long userId;

    private final String username;
    private final String lastname;
    private final String firstname;
    private Long lastMessageId;


    public UserInfo(Long userId, String username, String lastname, String firstname) {
        this.userId = userId;
        this.username = username;
        this.lastname = lastname;
        this.firstname = firstname;
    }


    public UserInfo setLastMessageId(Long lastMessageId) {
        this.lastMessageId = lastMessageId;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public Long getLastMessageId() {
        return lastMessageId;
    }
}

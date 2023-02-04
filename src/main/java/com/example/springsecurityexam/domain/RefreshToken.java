package com.example.springsecurityexam.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity @Table(name = "refresh_token")
@NoArgsConstructor
@Getter
public class RefreshToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "refresh_token", nullable = false)
    private String value;

    @Column(name = "created_date", nullable = false, insertable = false, updatable = false)
    private Date createdDate;

    public RefreshToken(String userId, String value) {
        this.userId = userId;
        this.value = value;
    }

    public void changRefreshToken(String value){
        this.value = value;
    }
}

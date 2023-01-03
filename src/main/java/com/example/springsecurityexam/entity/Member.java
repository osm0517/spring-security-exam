package com.example.springsecurityexam.entity;

import com.example.springsecurityexam.enumdata.RoleType;
import jakarta.persistence.*;
import lombok.*;

import java.util.concurrent.atomic.AtomicLong;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "user")
public class Member {

    /**
     * testCode를 작성할 때 보다 쉽게 작성을 하기 위함
     * userId는 unique 속성을 가지고 있어서 userId는 바꿔줘야함
     * @param userId
     * @return
     */
    public Member createUserMember(String userId){
        return new Member(userId, "testPwd", "testName", RoleType.USER);
    }

    /**
     * user뿐 아니라 admin 권한을 가지는 멤버를 만드는 함수
     * @param userId
     * @return
     */
    public Member createAdminMember(String userId){
        return new Member(userId, "testPwd", "testName", RoleType.USER);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private RoleType auth;

    public Member(String userId, String password, String name, RoleType auth){
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.auth = auth;
    }
}

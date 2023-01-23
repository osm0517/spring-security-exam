package com.example.springsecurityexam.domain;

import com.example.springsecurityexam.enumdata.RoleType;
import com.example.springsecurityexam.enumdata.StopState;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "user")
public class Member{

    /**
     * testCode를 작성할 때 보다 쉽게 작성을 하기 위함
     * userId는 unique 속성을 가지고 있어서 userId는 바꿔줘야함
     * @param userId
     * @return
     */
    public Member createUserMember(String userId){
        return new Member(userId, "testPwd", "testName", "testEmail", RoleType.USER);
    }

    /**
     * user뿐 아니라 admin 권한을 가지는 멤버를 만드는 함수
     * @param userId
     * @return
     */
    public Member createAdminMember(String userId){
        return new Member(userId, "testPwd", "testName", "testEmail", RoleType.ADMIN);
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private RoleType auth;

    @Column(name = "created_date", insertable = false, updatable = false, nullable = false)
    private Date createdDate;

    @Column(name = "updated_date", insertable = false, updatable = false, nullable = false)
    private Date updatedDate;

    @OneToMany(mappedBy = "producer")
    private List<Item> items = new ArrayList<>();

    @Enumerated(value = EnumType.STRING)
    @Column(name = "stop_state", insertable = false, nullable = false)
    private StopState stopState;

    @Column(name = "number_of_report", insertable = false, nullable = false)
    private int numberOfReport;

    public Member(String userId, String password, String name, String email, RoleType auth){
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.auth = auth;
    }

    public void changeStopState(StopState state){
        this.stopState = state;
    }
}

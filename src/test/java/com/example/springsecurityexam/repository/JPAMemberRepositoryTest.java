package com.example.springsecurityexam.repository;

import com.example.springsecurityexam.entity.Member;
import com.example.springsecurityexam.enumdata.RoleType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class JPAMemberRepositoryTest {

    @Autowired
    JPAMemberRepository memberRepository;

    @AfterEach
    void clearRepo(){
        memberRepository.deleteAllInBatch();
    }

    @Test
    void save() {
//        given
        Member member = new Member().createUserMember("test");

//        when
        Member member1 = memberRepository.save(member);

//        then
        Member findResult = memberRepository.findById(member1.getId());

        assertThat(member).usingRecursiveComparison().isEqualTo(findResult);
    }
    @Test
    void findByAuth() {
//        given
        Member member = new Member().createUserMember("test");
        Member member1 = new Member().createUserMember("testSave1");
        Member member2 = new Member().createAdminMember("testSave2");
        memberRepository.save(member);
        memberRepository.save(member1);
        memberRepository.save(member2);

//        when
        List<Member> admins = memberRepository.findByAuth(RoleType.ADMIN);
        List<Member> users = memberRepository.findByAuth(RoleType.USER);

//        then
        assertThat(admins)
                .usingRecursiveFieldByFieldElementComparator()
                .contains(member2);
        assertThat(users).size().isEqualTo(2);
        assertThat(users)
                .usingRecursiveFieldByFieldElementComparator()
                .contains(member, member1);
    }

    @Test
    void findAll() {
//        given
        Member member = new Member().createUserMember("test");
        Member member1 = new Member().createUserMember("testSave1");
        memberRepository.save(member);
        memberRepository.save(member1);

//        when
        List<Member> members = memberRepository.findAll();

//        then
        assertThat(members).size().isEqualTo(2);
        assertThat(members)
                .usingRecursiveFieldByFieldElementComparator()
                .contains(member, member1);
    }

    @DisplayName("userId로 회원을 찾기")
    @Test
    void findByUserId(){
//        given
        Member member = new Member().createUserMember("admin2");

//        when
        memberRepository.save(member);

        Member byId = memberRepository.findById(member.getId());
        Member byUserId = memberRepository.findByUserId(member.getUserId());

//        then
        assertThat(byUserId).usingRecursiveComparison()
                .isEqualTo(byId);
    }
}
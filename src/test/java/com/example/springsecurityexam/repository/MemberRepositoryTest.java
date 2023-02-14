package com.example.springsecurityexam.repository;

import com.example.springsecurityexam.domain.Member;
import com.example.springsecurityexam.enumdata.RoleType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    long allCount;

    void clear(List<Member> members){
        for (Member member : members) {
            memberRepository.delete(member);
        }
    }

    void clear(Member member){
        memberRepository.delete(member);
    }

//    개발환경에서만 테스트!!!!!!!!!!!!!!!!

//    @BeforeTestClass
//    void beforeClear(){
//        memberRepository.deleteAllInBatch();
//    }
//
//    @AfterEach
//    void clear(){
//        memberRepository.deleteAllInBatch();
//    }
//
    @BeforeTestClass
    void before(){
        allCount = memberRepository.count();
    }

//    @Test
//    @DisplayName("1개를 저장")
//    void save() {
////        given
//        Member member = new Member().createUserMember("test");
//
////        when
//        memberRepository.save(member);
//
////        then
//        Member findResult = memberRepository.findById(member.getId())
//                .orElseThrow(NoSuchElementException::new);
//
//        assertThat(member.getUserId()).isEqualTo(findResult.getUserId());
//    }
//    @Test
//    @DisplayName("권한에 해당하는 사용자 list를 확인")
//    void findByAuth() {
////        given
//        Member member = new Member().createUserMember("test");
//        Member member1 = new Member().createUserMember("testSave1");
//        Member member2 = new Member().createAdminMember("testSave2");
//
//        memberRepository.save(member);
//        memberRepository.save(member1);
//        memberRepository.save(member2);
//
////        when
//        List<Member> admins = memberRepository.findByAuth(RoleType.ADMIN);
//        List<Member> users = memberRepository.findByAuth(RoleType.USER);
//
////        then
//        assertThat(admins).size().isEqualTo(1);
//        assertThat(users).size().isEqualTo(2);
//    }

    @DisplayName("전체 찾기")
    @Test
    void findAllTest(){
//        given
        Member member = new Member().createUserMember(UUID.randomUUID().toString());
        Member member1 = new Member().createUserMember(UUID.randomUUID().toString());
        Member member2 = new Member().createAdminMember(UUID.randomUUID().toString());

        memberRepository.save(member);
        memberRepository.save(member1);
        memberRepository.save(member2);

//        when
        List<Member> members = memberRepository.findAll();

//        then
        assertThat(members).size().isEqualTo(allCount+3);
    }

    @Test
    @DisplayName("로그인 테스트")
    void loginTest(){

        Optional<Member> testUser = memberRepository.findByUserId("test");

        if(testUser.isEmpty()){
            Member testCreateUser = new Member().createUserMember("test");

            memberRepository.save(testCreateUser);

            Member user = memberRepository.findById(testCreateUser.getId())
                    .orElseThrow(NoSuchElementException::new);

            assertThat(user.getUserId()).isEqualTo(testCreateUser.getUserId());

        }else{
            Member user = testUser.get();

            Member findUser = memberRepository.findById(user.getId())
                    .orElseThrow(NoSuchElementException::new);

            assertThat(findUser.getUserId()).isEqualTo(user.getUserId());
        }

    }
}
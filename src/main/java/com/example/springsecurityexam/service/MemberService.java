package com.example.springsecurityexam.service;

import com.example.springsecurityexam.entity.Member;
import com.example.springsecurityexam.enumdata.RoleType;
import com.example.springsecurityexam.repository.JPAMemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class MemberService {

    private JPAMemberRepository memberRepository;

    private BCryptPasswordEncoder encoder;

    public MemberService(JPAMemberRepository memberRepository, BCryptPasswordEncoder encoder){
        this.memberRepository = memberRepository;
        this.encoder = encoder;
    }

    /**
     * 회원가입 로직
     * 성공하면 true, 실패하면 false
     * @param member
     * @return
     */
    public boolean signup(Member member){
//        사용자가 입력한 비밀번호(평문)
        String inputPassword = member.getPassword();

        Member encodedMember = passwordEncoder(member, inputPassword);
        memberRepository.save(encodedMember);

        Member saveResult = memberRepository.findById(encodedMember.getId());
        return !saveFalseCheck(saveResult);
    }

    /**
     * 성공하면 true, 실패하면 false
     * 실패하면 redirection
     * @param member
     * @return
     */
    public boolean login(Member member){
//        사용자가 입력한 비밀번호(평문)
        String userId = member.getUserId();
        String inputPassword = member.getPassword();

        Member findResult = memberRepository.findByUserId(userId);

        return encoder.matches(inputPassword, findResult.getPassword());
    }

    /**
     * 평문을 암호화하는 로직
     * @param member
     * @param inputPassword
     * @return
     */
    private Member passwordEncoder(Member member, String inputPassword) {
//        암호화가 된 비밀번호
        String password = encoder.encode(inputPassword);
//        암호화가 된 비밀번호로 다시 만든 객체
        return new Member(member.getUserId(), password, member.getName(), member.getEmail(), RoleType.USER);
    }

    /**
     * db에 정상적으로 저장됐는지 확인함
     * @param saveResult
     * @return
     */
    private static boolean saveFalseCheck(Member saveResult) {
        log.warn("member save fail");
        return saveResult == null;
    }

}

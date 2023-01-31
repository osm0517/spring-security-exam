package com.example.springsecurityexam.service;

import com.example.springsecurityexam.domain.BuyItem;
import com.example.springsecurityexam.domain.Member;
import com.example.springsecurityexam.dto.member.LoginDto;
import com.example.springsecurityexam.dto.member.PasswordEditDto;
import com.example.springsecurityexam.dto.member.UserInfoEditDto;
import com.example.springsecurityexam.enumdata.RoleType;
import com.example.springsecurityexam.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final BCryptPasswordEncoder encoder;

    private final List<DeleteAccount> deleteAccounts;

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

//        Member saveResult = memberRepository.findById(encodedMember.getId());
//        return !saveFalseCheck(saveResult);
        return true;
    }

    /**
     * 성공하면 true, 실패하면 false
     * 실패하면 redirection
     * @param member
     * @return
     */
    public Member login(LoginDto member){
//        사용자가 입력한 비밀번호(평문)
        String userId = member.getUserId();
        String inputPassword = member.getPassword();

        Member findResult = memberRepository.findByUserId(userId)
                .orElseThrow(NoSuchElementException::new);

        if (encoder.matches(inputPassword, findResult.getPassword())){
            return findResult;
        }
        return null;
    }

    /**
     * home 에서 session을 받고 거기서 받은 아이디에 정보가 정확한지를 확인
     */
    public Member checkSession(long userId){
        log.debug("no such");
        return memberRepository.findById(userId)
                .orElseThrow(NoSuchElementException::new);
    }

    public void delete(String type, long userId, String value){
        for (DeleteAccount clazz : deleteAccounts) {
            if(clazz.support(type)){
                clazz.delete(userId, value);
            }
        }
    }

    /**
     * home 에서 session을 받고 거기서 받은 아이디에 정보가 정확한지를 확인 후
     * 수정 폼에서 사용할 수 있도록 비밀번호를 평문으로 보내줌
     */
    public Member updateUserInfo(long userId, UserInfoEditDto dto){
        Member member = memberRepository.findById(userId)
                .orElseThrow(NoSuchElementException::new);

        member.changeUserInfo(dto.getName(), dto.getEmail());

        memberRepository.save(member);

        return member;
    }

    /**
     * home 에서 session을 받고 거기서 받은 아이디에 정보가 정확한지를 확인 후
     * password를 수정
     */
    public void updatePassword(long userId,PasswordEditDto dto){
        if(dto.getPassword().equals(dto.getConfirm())){
            String encodedPassword = encoder.encode(dto.getPassword());

            Member member = memberRepository.findById(userId)
                    .orElseThrow(NoSuchElementException::new);
            member.changePassword(encodedPassword);
        }
        throw new IllegalArgumentException("not match password and confirm");
    }

    public List<BuyItem> findBuyItems(long userId){
        Member member = memberRepository.findById(userId)
                .orElseThrow(NoSuchElementException::new);

        return member.getBuyItems();
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

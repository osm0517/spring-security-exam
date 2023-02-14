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
     */
    public boolean signup(Member member){
//        사용자가 입력한 비밀번호(평문)
        String inputPassword = member.getPassword();

        Member encodedMember = passwordEncoder(member, inputPassword);
        memberRepository.save(encodedMember);

        return true;
    }

    /**
     * 성공하면 true, 실패하면 false
     * 실패하면 redirection
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
    public Member checkUserId(String userId){
        return memberRepository.findByUserId(userId)
                .orElseThrow(NoSuchElementException::new);
    }

    /**
     * 없으면 null을 반환
     */
    public Member checkUsername(String username){
        return memberRepository.findByUserId(username)
                .orElse(null);
    }

    public void delete(String type, String userId, String value){
        for (DeleteAccount clazz : deleteAccounts) {
            if(clazz.support(type)){
                clazz.delete(userId, value);
            }
        }
    }

    /**
     * userId로 계정을 찾아서 이름과 이메일을 변경할 수 있음
     */
    public Member updateUserInfo(String userId, UserInfoEditDto dto){

        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(NoSuchElementException::new);

        member.changeUserInfo(dto.getName(), dto.getEmail());

        return memberRepository.save(member);
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
        }else {
            throw new IllegalArgumentException("not match password and confirm");
        }
    }

    public List<BuyItem> findBuyItems(String userId){

        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(NoSuchElementException::new);

        return member.getBuyItems();
    }

    /**
     * 평문을 암호화하는 로직
     */
    private Member passwordEncoder(Member member, String inputPassword) {
//        암호화가 된 비밀번호
        String password = encoder.encode(inputPassword);
//        암호화가 된 비밀번호로 다시 만든 객체
        return new Member(member.getUserId(), password, member.getName(), member.getEmail(), RoleType.USER);
    }

    /**
     * db에 정상적으로 저장됐는지 확인함
     */
    private static boolean saveFalseCheck(Member saveResult) {
        log.warn("member save fail");
        return saveResult == null;
    }

}

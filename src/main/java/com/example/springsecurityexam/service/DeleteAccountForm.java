package com.example.springsecurityexam.service;

import com.example.springsecurityexam.domain.Member;
import com.example.springsecurityexam.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DeleteAccountForm implements DeleteAccount{

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;

    @Override
    public boolean support(String type) {
        return type.equals("form");
    }

    @Override
    public boolean delete(String userId, String value) {
        Member user = memberRepository.findByUserId(userId)
                .orElseThrow(NoSuchElementException::new);
        if(encoder.matches(value, user.getPassword())){
            System.out.println("비밀번호 동일");
            memberRepository.delete(user);
            return true;
        }
        return false;
    }
}

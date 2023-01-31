package com.example.springsecurityexam.service;

import com.example.springsecurityexam.domain.Member;
import com.example.springsecurityexam.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class DeleteAccountOAuth implements DeleteAccount{

    private final MemberRepository memberRepository;

    @Override
    public boolean support(String type) {
        return type.equals("oauth");
    }

    @Override
    public boolean delete(long userId, String value) {
        Member user = memberRepository.findById(userId)
                .orElseThrow(NoSuchElementException::new);
        if(Objects.equals(user.getEmail(), value)){
            memberRepository.delete(user);
            return true;
        }
        return false;
    }
}

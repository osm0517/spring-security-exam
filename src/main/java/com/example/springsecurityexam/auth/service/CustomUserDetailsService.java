package com.example.springsecurityexam.auth.service;

import com.example.springsecurityexam.auth.CustomUserDetails;
import com.example.springsecurityexam.domain.Member;
import com.example.springsecurityexam.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    MemberRepository memberRepository;

    public CustomUserDetailsService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    /**
     * userId 를 파라미터로 받아서 repository 에서 계정을 받음
     * 계정을 찾으면 userDetails 를 반환하여 저장
     * @param userId the username identifying the user whose data is required.
     * @return CustomUserDetails
     * @throws UsernameNotFoundException
     */
    @Override
    public CustomUserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Member member = memberRepository.findByUserId(userId);
        if(member != null){
            return new CustomUserDetails(member);
        }

        return null;
    }
}

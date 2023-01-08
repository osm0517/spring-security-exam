package com.example.springsecurityexam.auth.service;

import com.example.springsecurityexam.auth.CustomUserDetails;
import com.example.springsecurityexam.entity.Member;
import com.example.springsecurityexam.repository.JPAMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomUserDetailsService implements UserDetailsService {

    JPAMemberRepository memberRepository;

    public CustomUserDetailsService(JPAMemberRepository memberRepository){
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

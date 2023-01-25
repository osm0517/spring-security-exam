package com.example.springsecurityexam.auth.service;

import com.example.springsecurityexam.auth.userDetails.CustomUserDetails;
import com.example.springsecurityexam.domain.Member;
import com.example.springsecurityexam.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;
    /**
     * userId 를 파라미터로 받아서 repository 에서 계정을 받음
     * 계정을 찾으면 userDetails 를 반환하여 저장
     * @param userId the username identifying the user whose data is required.
     * @return CustomUserDetails
     * @throws UsernameNotFoundException
     */
    @Override
    public CustomUserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        log.debug("loadUserByUserName");
        Member member = memberRepository.findByUserId(userId);
        if(member != null){
            log.debug("member");
            return new CustomUserDetails(member);
        }
        log.debug("member not found");
        throw new UsernameNotFoundException("username not found");
    }
}

package com.example.springsecurityexam.service;

import com.example.springsecurityexam.repository.JPAMemberRepository;
import com.example.springsecurityexam.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

}

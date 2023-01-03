package com.example.springsecurityexam.service;

import com.example.springsecurityexam.repository.JPAMemberRepository;
import com.example.springsecurityexam.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    @Autowired
    private JPAMemberRepository memberRepository;



}

package com.example.springsecurityexam.service;

import com.example.springsecurityexam.entity.Member;
import com.example.springsecurityexam.repository.JPAMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    @Autowired
    private JPAMemberRepository memberRepository;

    public String signup(Member member){



        return "Y";
    }

}

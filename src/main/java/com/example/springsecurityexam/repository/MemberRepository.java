package com.example.springsecurityexam.repository;

import com.example.springsecurityexam.dto.Member;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;


public interface MemberRepository {

    public Member save(Member member);

    public Optional<Member> findById(String name);

    public Optional<Member> findByName(AtomicLong id);

//    public Member login(Member member);

    public void delete(Member member);

    public List<Member> findAll();
}

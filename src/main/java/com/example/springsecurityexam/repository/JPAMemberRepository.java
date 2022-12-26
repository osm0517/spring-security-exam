package com.example.springsecurityexam.repository;

import com.example.springsecurityexam.dto.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public interface JPAMemberRepository extends JpaRepository<Member, AtomicLong>, MemberRepository {
    @Override
    Member save(Member member);

    @Override
    Optional<Member> findById(String name);

    @Override
    Optional<Member> findByName(AtomicLong id);

}

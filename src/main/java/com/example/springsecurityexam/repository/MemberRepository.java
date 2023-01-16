package com.example.springsecurityexam.repository;

import com.example.springsecurityexam.domain.Member;
import com.example.springsecurityexam.enumdata.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {

    Member save(Member member);

    Member findById(int id);

    List<Member> findByAuth(RoleType auth);

    List<Member> findAll();

    Member findByUserId(String userId);
    @Override
    void deleteAllInBatch();
}

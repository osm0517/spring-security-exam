package com.example.springsecurityexam.repository;

import com.example.springsecurityexam.domain.Member;
import com.example.springsecurityexam.enumdata.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByAuth(RoleType auth);

    Optional<Member> findByUserId(String userId);

}

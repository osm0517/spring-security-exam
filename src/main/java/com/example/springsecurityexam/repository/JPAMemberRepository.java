package com.example.springsecurityexam.repository;

import com.example.springsecurityexam.entity.Member;
import com.example.springsecurityexam.enumdata.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public interface JPAMemberRepository extends JpaRepository<Member, Integer> {

    Member save(Member member);

    Member findById(int id);

    List<Member> findByAuth(RoleType auth);

    List<Member> findAll();


    @Override
    void deleteAllInBatch();
}

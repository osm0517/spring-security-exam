package com.example.springsecurityexam.repository;

import com.example.springsecurityexam.domain.UserReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserReportRepository extends JpaRepository<UserReport, Integer> {

    UserReport save(int userId);
    UserReport findByUserId(int userId);
    List<UserReport> findAll();
}

package com.example.springsecurityexam.entity;

import jakarta.persistence.*;

import java.util.Date;

//@Entity
//@Table(name = "user_report")
public class UserReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private int id;

    @Column(name = "user_user_id", nullable = false)
    private String userId;

    @Column(name = "number_of_report")
    private int numberOfReport;

    @Column(name = "first_report_date", insertable = false, nullable = false, updatable = false)
    private Date firstReportDate;

    @Column(name = "latest_report_date", insertable = false, nullable = false, updatable = false)
    private Date latestReportDate;
}

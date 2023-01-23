package com.example.springsecurityexam.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.Date;

//@Getter
//@Entity
//@Table(name = "user_report")
public class UserReport {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private int userId;

    @Column(name = "number_of_report")
    private int numberOfReport;

    @Column(name = "first_report_date", insertable = false, nullable = false, updatable = false)
    private Date firstReportDate;

    @Column(name = "latest_report_date", insertable = false, nullable = false, updatable = false)
    private Date latestReportDate;

    public UserReport(int userId){
        this.userId = userId;
    }
}

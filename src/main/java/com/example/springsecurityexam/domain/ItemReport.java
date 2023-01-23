package com.example.springsecurityexam.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "item_report")
public class ItemReport {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_report_id")
    private int id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private int itemId;

    @Column(name = "number_of_report", insertable = false)
    private int numberOfReport;

    @Column(name = "first_report_date", insertable = false, nullable = false, updatable = false)
    private Date firstReportDate;

    @Column(name = "latest_report_date", insertable = false, nullable = false, updatable = false)
    private Date latestReportDate;

    public ItemReport(int itemId){
        this.itemId = itemId;
        this.numberOfReport = 1;
    }

    public void changeNumber(int number){
        this.numberOfReport = number;
    }
}

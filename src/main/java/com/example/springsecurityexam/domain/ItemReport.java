package com.example.springsecurityexam.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

//@Getter
////@Entity
//@NoArgsConstructor
//@Table(name = "item_report")
//@PrimaryKeyJoinColumn(name = "id")
public class ItemReport {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(fetch = FetchType.LAZY)
    private Item item;

    @Column(name = "number_of_report", insertable = false)
    private int numberOfReport;

    @Column(name = "first_report_date", insertable = false, nullable = false, updatable = false)
    private Date firstReportDate;

    @Column(name = "latest_report_date", insertable = false, nullable = false, updatable = false)
    private Date latestReportDate;

    public ItemReport(Item item){
        if(item == null){
            throw new IllegalArgumentException("item object null");
        }
        this.item = item;
        this.numberOfReport = 1;
    }

    public void changeNumber(int number){
        this.numberOfReport = number;
    }
}

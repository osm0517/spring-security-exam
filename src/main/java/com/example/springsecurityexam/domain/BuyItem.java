package com.example.springsecurityexam.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "buy_item")
@Getter
@NoArgsConstructor
public class BuyItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "created_date",nullable = false,
            insertable = false, updatable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate buyDate;

    public BuyItem(Member member, Item item, int quantity){
        item.buyItem(item.getQuantity() - quantity);
        this.quantity = quantity;
        this.member = member;
        this.item = item;
    }

}

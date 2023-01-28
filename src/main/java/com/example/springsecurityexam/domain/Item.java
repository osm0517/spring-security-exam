package com.example.springsecurityexam.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "item")
@EqualsAndHashCode
public class Item {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String itemName;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="producer")
    private Member producer;

    @Column(nullable = false, insertable = false, updatable = false)
    private Date createdDate;

    @Column(nullable = false, insertable = false, updatable = false)
    private Date updatedDate;

    @Column(name = "number_of_report", insertable = false, nullable = false)
    private int numberOfReport;

    public Item(String itemName, int price, int quantity, Member member){
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
        if(member == null){
            throw new IllegalArgumentException("member object null");
        }
        this.producer = member;
        producer.getItems().add(this);
    }

    public void updateItem(String itemName, int price, int quantity){
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }

    public void buyItem(int quantity){
        this.quantity = quantity;
    }

//    @Builder
//    public Item(String itemName, int price, int quantity, int producer){
//        this.itemName = itemName;
//        this.price = price;
//        this.quantity = quantity;
//        this.producer = producer;
//    }
}

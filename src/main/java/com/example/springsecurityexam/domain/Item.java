package com.example.springsecurityexam.domain;

import com.example.springsecurityexam.dto.ItemAddDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Setter
    @Column(name = "name", nullable = false)
    private String itemName;

    @Setter
    @Column(nullable = false)
    private int price;

    @Setter
    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int producer;

    @Column(nullable = false, insertable = false, updatable = false)
    private Date createdDate;

    @Column(nullable = false, insertable = false, updatable = false)
    private Date updatedDate;

    @Builder
    public Item(String itemName, int price, int quantity){
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
        this.producer = 1;
    }

//    @Builder
//    public Item(String itemName, int price, int quantity, int producer){
//        this.itemName = itemName;
//        this.price = price;
//        this.quantity = quantity;
//        this.producer = producer;
//    }
}

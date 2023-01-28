package com.example.springsecurityexam.dto.item;

import lombok.Data;

@Data
public class ItemAddDto {

    private String ItemName;
    private int price;
    private int quantity;
}

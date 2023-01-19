package com.example.springsecurityexam.dto;

import lombok.Data;

@Data
public class ItemAddDto {

    private String ItemName;
    private int price;
    private int quantity;
}

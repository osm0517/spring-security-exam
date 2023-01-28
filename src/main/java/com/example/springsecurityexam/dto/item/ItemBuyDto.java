package com.example.springsecurityexam.dto.item;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemBuyDto {

    private String itemName;
    private int price;
    private int quantity;
    private int buyQuantity;

    public ItemBuyDto(String itemName, int price, int quantity){
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}

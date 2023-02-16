package com.example.springsecurityexam.dto.item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemBuyDto {

    @NotNull
    private long itemId;

    private String itemName;

    private int price;

    private int quantity;

    @Min(1)
    private int buyQuantity;

    public ItemBuyDto(String itemName, int price, int buyQuantity){
        this.itemName = itemName;
        this.price = price;
        this.buyQuantity = buyQuantity;
    }
}

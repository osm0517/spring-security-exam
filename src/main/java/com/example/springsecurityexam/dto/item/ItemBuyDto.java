package com.example.springsecurityexam.dto.item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemBuyDto {

    @NotBlank
    private String itemName;

    private int price;

    @Min(1)
    private int quantity;

    @Min(1)
    private int buyQuantity;

    public ItemBuyDto(String itemName, int price, int quantity){
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}

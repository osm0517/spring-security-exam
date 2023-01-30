package com.example.springsecurityexam.dto.item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemSaveDto {

    @NotBlank
    private String itemName;

    @NotNull
    @Min(100)
    private Integer price;

    @NotNull
    @Min(1000)
    private Integer quantity;
}

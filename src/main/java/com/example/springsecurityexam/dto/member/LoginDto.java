package com.example.springsecurityexam.dto.member;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDto {

    @NotBlank
    private String userId;

    @NotBlank
    @Min(8)
    private String password;
}

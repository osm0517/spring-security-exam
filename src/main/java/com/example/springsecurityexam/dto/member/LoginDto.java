package com.example.springsecurityexam.dto.member;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {

    @NotBlank
    private String userId;

    @NotBlank
    @Min(8)
    private String password;
}

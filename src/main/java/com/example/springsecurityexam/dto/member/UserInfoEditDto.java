package com.example.springsecurityexam.dto.member;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoEditDto {

    @NotBlank
    private String name;

    @NotBlank
    private String email;
}

package com.example.springsecurityexam.dto.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoEditDto {

    @NotBlank
    private String name;

    @Null
    private String email;
}

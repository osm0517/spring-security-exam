package com.example.springsecurityexam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoEditDto {

    private String name;
    private String email;
}

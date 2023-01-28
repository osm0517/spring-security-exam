package com.example.springsecurityexam.dto.member;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class PasswordEditDto {

    private String password;
    private String confirm;
}

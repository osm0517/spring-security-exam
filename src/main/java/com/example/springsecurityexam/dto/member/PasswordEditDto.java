package com.example.springsecurityexam.dto.member;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class PasswordEditDto {

    @NotBlank
    private String password;

    @NotBlank
    private String confirm;
}

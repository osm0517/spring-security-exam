package com.example.springsecurityexam.dto.member;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordEditDto {

    @NotBlank
    private String password;

    @NotBlank
    private String confirm;
}

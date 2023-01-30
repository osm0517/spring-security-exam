package com.example.springsecurityexam.dto.member;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MemberSaveDto {

    @NotBlank
    private String userId;

    @NotBlank
    @Size(min = 8)
    private String password;

    @NotBlank
    @Size(min = 8)
    private String passwordConfirm;

    @NotBlank
    @Size(min = 2)
    private String name;

    @NotBlank
    @Email(regexp = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$")
    private String email;

//    중복확인을 했는지를 확인하는 것
//    @NotNull
//    private boolean idOverLap;
//
//    @NotNull
//    private boolean emailOverLap;


}

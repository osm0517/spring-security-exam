package com.example.springsecurityexam.dto.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class DeleteMemberDto {

    @NotBlank
//    @Pattern(regexp = "본인확인을 위해서 (이메일:비밀번호)을 입력해주세요")
    private String value;

    @NotBlank
    @Pattern(regexp = "계정을 삭제합니다")
    private String validatedText;
}

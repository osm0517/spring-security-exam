package com.example.springsecurityexam.dto;

import com.example.springsecurityexam.enumdata.ItemReportReason;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReportDto {

    @NotBlank
    private String name;

    @NotNull
    private ItemReportReason reason;

    private String etc;
}

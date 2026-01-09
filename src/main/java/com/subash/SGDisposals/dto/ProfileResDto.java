package com.subash.SGDisposals.dto;

import com.subash.SGDisposals.RoleEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProfileResDto {

    @NotBlank
    private String user_name;

    @NotBlank
    private String email;

    @NotNull
    private RoleEnum role;

    @NotNull
    private LocalDate joined_at;

    @NotNull
    private float total_points;

    @NotNull
    private int current_points;
}

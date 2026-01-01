package com.subash.SGDisposals.dto;

import com.subash.SGDisposals.StatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddNewRequestDto {

    @NotNull
    private Long user;

    @NotBlank
    @Size(min = 1, max = 200)
    private String address;
}

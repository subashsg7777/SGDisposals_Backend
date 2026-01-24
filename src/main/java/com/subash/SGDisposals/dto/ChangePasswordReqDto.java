package com.subash.SGDisposals.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordReqDto {

    @NotBlank
    @Email
    String email;

    @NotBlank
    @Size(min = 8)
    String password;
}

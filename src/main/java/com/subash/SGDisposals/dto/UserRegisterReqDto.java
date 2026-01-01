package com.subash.SGDisposals.dto;

import com.subash.SGDisposals.RoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterReqDto {

    @NotBlank
    @Size(min = 1, max = 20)
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min=8)
    private String password;

    @NotNull
    private RoleEnum role = RoleEnum.USER;

    @NotBlank
    private String Transactional_password;
}

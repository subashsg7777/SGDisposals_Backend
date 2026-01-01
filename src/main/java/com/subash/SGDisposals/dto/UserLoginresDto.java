package com.subash.SGDisposals.dto;

import com.subash.SGDisposals.RoleEnum;
import lombok.Data;

@Data
public class UserLoginresDto {

    private String email;
    private String password;
    private String message;
    private Long id;
    private RoleEnum role;
}

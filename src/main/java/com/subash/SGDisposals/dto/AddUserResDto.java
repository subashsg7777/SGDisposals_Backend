package com.subash.SGDisposals.dto;

import com.subash.SGDisposals.RoleEnum;
import lombok.Data;

@Data
public class AddUserResDto {
    private String message;
    private String name;
    private String email;
    private RoleEnum role;
    private Long id;
}

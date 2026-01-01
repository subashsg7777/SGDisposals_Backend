package com.subash.SGDisposals.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddAddressReqDto {

    @NotNull
    private Long user_id;

    @NotBlank
    private String address;
}

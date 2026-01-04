package com.subash.SGDisposals.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BuyProductReqDto {

    @NotNull
    private Long user_id;

    @NotNull
    private Long product_id;

    @NotBlank
    private String transactionalPassword;

    @NotNull
    private int quantity;
}

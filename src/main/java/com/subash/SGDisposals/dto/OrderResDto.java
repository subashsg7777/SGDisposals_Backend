package com.subash.SGDisposals.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderResDto {

    @NotNull
    private String order_id;

    @NotNull
    private double product_id;

    @NotNull
    private int quantity;

    @NotNull
    private double price;
}

package com.subash.SGDisposals.dto;

import lombok.Data;

@Data
public class ProductBuyResDto {

    private String message;

    private String product_name;

    private Long product_id;

    private int quantity;
}

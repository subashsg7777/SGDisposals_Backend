package com.subash.SGDisposals.dto;

import com.subash.SGDisposals.entity.Product;
import lombok.Data;

import java.util.List;

@Data
public class AllProductsResponseDto {

    private List<Product> products;
}

package com.subash.SGDisposals.dto;

import com.subash.SGDisposals.entity.Product;
import lombok.Data;

import java.util.List;

@Data
public class AllProductsResponseDto {

    private List<ProductDto> products;

    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
}

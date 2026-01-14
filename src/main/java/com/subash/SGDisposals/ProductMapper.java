package com.subash.SGDisposals;

import com.subash.SGDisposals.dto.ProductDto;
import com.subash.SGDisposals.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductDto toDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(Long.valueOf(product.getId()));
        dto.setName(product.getName());
        dto.setPrice(Double.valueOf(product.getPoints()));
        dto.setImageUrl(product.getImage());
        dto.setDescription(product.getDescription());
        return dto;
    }
}

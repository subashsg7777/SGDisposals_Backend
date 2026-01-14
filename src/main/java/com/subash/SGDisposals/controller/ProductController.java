package com.subash.SGDisposals.controller;

import com.subash.SGDisposals.ProductMapper;
import com.subash.SGDisposals.dto.AllProductsResponseDto;
import com.subash.SGDisposals.dto.BuyProductReqDto;
import com.subash.SGDisposals.dto.OrderResDto;
import com.subash.SGDisposals.dto.ProductDto;
import com.subash.SGDisposals.entity.Product;
import com.subash.SGDisposals.service.IProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/v2/product")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;
    private final ProductMapper productMapper;

    @GetMapping
    public ResponseEntity<AllProductsResponseDto> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size
    ){
        log.info("Page No and size for paginbation : ",page, size);
        Page<Product> results = productService.getAllProducts(page,size);

        List<ProductDto> productDtos = results.getContent().stream().map(product -> productMapper.toDto(product) ).toList();

        AllProductsResponseDto allProductsResponseDto = new AllProductsResponseDto();
        allProductsResponseDto.setProducts(productDtos);
        allProductsResponseDto.setPage(results.getNumber());
        allProductsResponseDto.setSize(results.getSize());
        allProductsResponseDto.setTotalElements(results.getTotalElements());
        allProductsResponseDto.setTotalPages(results.getTotalPages());
        allProductsResponseDto.setLast(results.isLast());
        return ResponseEntity.ok(allProductsResponseDto);
    }

    @PostMapping("buy")
    public ResponseEntity<?> buyProduct(@Valid  @RequestBody BuyProductReqDto buyProductReqDto){

        OrderResDto result = productService.buyProduct(buyProductReqDto);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("cancel")
    public ResponseEntity<?> cancelOrder(@NotNull  @RequestParam Long id){

        boolean result = productService.cancelOrder(id);
        if(result){
            return ResponseEntity.ok("Order Cancellation is Successful");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order Cancellation is Not Successful");
    }

    @PutMapping("deliver")
    public ResponseEntity<?> deliverOrder(@NotNull  @RequestParam Long id){
        boolean result = productService.deliverOrder(id);
        if(result){
            return ResponseEntity.ok("Order is Delivered Successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order is Not Successful");
    }
}

package com.subash.SGDisposals.controller;

import com.subash.SGDisposals.dto.AllProductsResponseDto;
import com.subash.SGDisposals.dto.BuyProductReqDto;
import com.subash.SGDisposals.dto.ProductBuyResDto;
import com.subash.SGDisposals.entity.Product;
import com.subash.SGDisposals.service.IProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;

    @GetMapping
    public ResponseEntity<?> getAllProducts(){
        List<Product> results = productService.getAllProducts();
        AllProductsResponseDto allProductsResponseDto = new AllProductsResponseDto();
        allProductsResponseDto.setProducts(results);
        return ResponseEntity.ok(allProductsResponseDto);
    }

    @PostMapping("buy")
    public ResponseEntity<?> buyProduct(@Valid  @RequestBody BuyProductReqDto buyProductReqDto){

        Map result = productService.buyProduct(buyProductReqDto);
        ProductBuyResDto  productBuyResDto = new ProductBuyResDto();

        if((boolean) result.get("result")){
            productBuyResDto.setMessage("Order is Successful");
            productBuyResDto.setQuantity(buyProductReqDto.getQuantity());
            productBuyResDto.setProduct_id(buyProductReqDto.getProduct_id());
            productBuyResDto.setProduct_name(result.get("product").toString());
            return  ResponseEntity.ok(productBuyResDto);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order is Not Successful");
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

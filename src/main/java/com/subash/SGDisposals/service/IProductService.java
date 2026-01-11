package com.subash.SGDisposals.service;

import com.subash.SGDisposals.dto.BuyProductReqDto;
import com.subash.SGDisposals.dto.OrderResDto;
import com.subash.SGDisposals.entity.Product;

import java.util.List;
import java.util.Map;

public interface IProductService {

    List<Product> getAllProducts();
    OrderResDto buyProduct(BuyProductReqDto buyProductReqDto);

    boolean cancelOrder(Long id);

    boolean deliverOrder(Long id);
}

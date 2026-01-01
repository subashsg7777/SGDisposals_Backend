package com.subash.SGDisposals.service.implementation;

import com.subash.SGDisposals.OrderStatusEnum;
import com.subash.SGDisposals.RoleEnum;
import com.subash.SGDisposals.dto.BuyProductReqDto;
import com.subash.SGDisposals.entity.Order;
import com.subash.SGDisposals.entity.Product;
import com.subash.SGDisposals.entity.User;
import com.subash.SGDisposals.exception.InvalidRequestStateException;
import com.subash.SGDisposals.exception.OrderException;
import com.subash.SGDisposals.exception.ResourceNotFoundException;
import com.subash.SGDisposals.exception.UnauthorizedRequestException;
import com.subash.SGDisposals.repositories.OrderRepo;
import com.subash.SGDisposals.repositories.ProductRepo;
import com.subash.SGDisposals.repositories.UserRepo;
import com.subash.SGDisposals.service.IProductService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepo productRepo;
    private final UserRepo userRepo;
    private final OrderRepo orderRepo;


    @Override
    public List<Product> getAllProducts() {

        List<Product> products = productRepo.findAll();
        if (products.isEmpty()){
            throw new ResourceNotFoundException("No Products found");
        }
        return products;
    }

    @Transactional
    @Override
    public Map buyProduct(BuyProductReqDto buyProductReqDto) {

        Product product = productRepo.findById(buyProductReqDto.getProduct_id())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        User user = userRepo.findById(buyProductReqDto.getUser_id())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getPoints() <= (product.getPoints() * buyProductReqDto.getQuantity())) {
            throw new InvalidRequestStateException("Insufficient points");
        }

        if (!user.getTransactional_password().equals(buyProductReqDto.getTransactional_password())) {
            throw new UnauthorizedRequestException("Invalid Transaction Password");
        }

        if (!(user.getRole() == RoleEnum.USER)) {
            throw new UnauthorizedRequestException("Only User Can Buy Products");
        }

        if (product.getInStock() < buyProductReqDto.getQuantity()) {
            throw new ResourceNotFoundException("Not Enough Stock In Inventory");
        }

        try {
            Order order = new Order();
            order.setProductId(Double.valueOf(product.getId()));
            order.setUserId(Double.valueOf(user.getId()));
            order.setQuanity((double) buyProductReqDto.getQuantity());
            order.setStatus(OrderStatusEnum.IN_PROGRESS);
            orderRepo.save(order);

            user.setPoints((int) (user.getPoints() - (product.getPoints() * buyProductReqDto.getQuantity())));
            userRepo.save(user);

            product.setInStock(product.getInStock() - buyProductReqDto.getQuantity());
            productRepo.save(product);

            order.setStatus(OrderStatusEnum.ORDERED);
            Map map = new HashMap();
            map.put("result",true);
            map.put("product",product.getName());
            return map;
        } catch (Exception e) {
            throw new OrderException("Can't Complete Order Purchase Right Now Try Again Later !...");
        }
    }

    @Override
    public boolean cancelOrder(Long id) {

        Order order = orderRepo.findById(Math.toIntExact(id)).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (order.getStatus() == OrderStatusEnum.CANCELED) {
            throw new OrderException("Order Already Cancelled");
        }

        if (order.getStatus() == OrderStatusEnum.ORDERED) {
            throw new  OrderException("Order Already Completed");
        }

        order.setStatus(OrderStatusEnum.CANCELED);
        orderRepo.save(order);
        return true;
    }

    @Override
    public boolean deliverOrder(Long id) {

        Order order = orderRepo.findById(Math.toIntExact(id)).orElseThrow(() -> {
            throw new ResourceNotFoundException("Order Not Found");
        });

        if (order.getStatus() == OrderStatusEnum.ORDERED) {
            throw new InvalidRequestStateException("Order Already Completed");
        }

        if (order.getStatus() == OrderStatusEnum.CANCELED) {
            throw new InvalidRequestStateException("Order Already Cancelled");
        }

        order.setStatus(OrderStatusEnum.ORDERED);
        orderRepo.save(order);
        return true;
    }

}

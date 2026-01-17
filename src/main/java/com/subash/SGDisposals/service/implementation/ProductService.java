package com.subash.SGDisposals.service.implementation;

import com.subash.SGDisposals.OrderStatusEnum;
import com.subash.SGDisposals.RoleEnum;
import com.subash.SGDisposals.dto.BuyProductReqDto;
import com.subash.SGDisposals.dto.OrderResDto;
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
import com.subash.SGDisposals.service.EmailService;
import com.subash.SGDisposals.service.IProductService;
import jakarta.validation.constraints.Email;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepo productRepo;
    private final UserRepo userRepo;
    private final OrderRepo orderRepo;
    private final EmailService emailService;

    private String generateOrderId(){
        String placeholders = "SG_DP_OR_";
        String dateTime = LocalDateTime.now().toString();
        return placeholders.concat(dateTime);
    }

    @Cacheable
    @Override
    public Page<Product> getAllProducts(int pageno, int size) {
        Pageable pageable = PageRequest.of(pageno,size);
        return productRepo.findAll(pageable);
    }

    @Transactional
    @Override
    public OrderResDto buyProduct(BuyProductReqDto buyProductReqDto) {

        Product product = productRepo.findById(buyProductReqDto.getProduct_id())
                .orElseThrow(() -> new ResourceNotFoundException("Please Select An Valid Product"));

        User user = userRepo.findById(buyProductReqDto.getUser_id())
                .orElseThrow(() -> new UnauthorizedRequestException("User is not Available Please. Check Credentials Carefully"));

        if (user.getPoints() <= (product.getPoints() * buyProductReqDto.getQuantity())) {
            throw new InvalidRequestStateException("Insufficient points To Buy This Product");
        }

        if (!user.getTransactional_password().equals(buyProductReqDto.getTransactionalPassword())) {
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
            String order_id = generateOrderId();
            order.setOrder_id(order_id);
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
            OrderResDto orderResDto = new OrderResDto();
            orderResDto.setOrder_id(order_id);
            orderResDto.setProduct_id(buyProductReqDto.getProduct_id());
            orderResDto.setQuantity(buyProductReqDto.getQuantity());
            orderResDto.setPrice(product.getPoints() * buyProductReqDto.getQuantity());

            emailService.sendOrderReceipt(user.getEmail(),order_id,product.getId(),product.getName(),
                    product.getPoints(),buyProductReqDto.getQuantity(),product.getPoints() * buyProductReqDto.getQuantity());

            return orderResDto;
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

        if(order.getStatus() == OrderStatusEnum.IN_PROGRESS){
            throw new InvalidRequestStateException("Order Is not Complete Yet Please Wait Until the Process is complete");
        }

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

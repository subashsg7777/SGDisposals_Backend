package com.subash.SGDisposals.entity;

import com.subash.SGDisposals.OrderStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "order_id",nullable = false)
    private String order_id;

    @NotNull
    @Column(name = "product_id", nullable = false)
    private Double productId;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Double userId;

    @NotNull
    @Column(name = "quanity", nullable = false)
    private Double quanity;

    @NotNull
    @ColumnDefault("'IN_PROGRESS'")
    @Lob
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatusEnum status;

}
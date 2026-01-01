package com.subash.SGDisposals.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 45)
    @NotNull
    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @NotNull
    @Column(name = "points", nullable = false)
    private Float points;

    @NotNull
    @Column(name = "tax", nullable = false)
    private Float tax;

    @NotNull
    @Column(name = "in_stock", nullable = false)
    private Double inStock;

    @Size(max = 45)
    @Column(name = "label", length = 45)
    private String label;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    private String description;

    @Size(max = 500)
    @Column(name = "image", length = 500)
    private String image;

}
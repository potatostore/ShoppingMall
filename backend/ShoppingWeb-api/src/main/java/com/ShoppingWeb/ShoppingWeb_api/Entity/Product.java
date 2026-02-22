package com.ShoppingWeb.ShoppingWeb_api.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity(name = "${mallDB.product.url}")
@Table(name = "${mallDB.product.url}")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Setter
    private String name;
    @Setter
    private int price;

    public void setId(Integer id) {
        this.id = id;
    }
}
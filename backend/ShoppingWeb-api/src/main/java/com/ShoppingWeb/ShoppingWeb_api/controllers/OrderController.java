package com.ShoppingWeb.ShoppingWeb_api.controllers;

import com.ShoppingWeb.ShoppingWeb_api.Entity.Order;
import com.ShoppingWeb.ShoppingWeb_api.Repository.OrderRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }

    @GetMapping
    public List<Order> getOrder(){
        return orderRepository.findAll();
    }

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable Long id){
        return orderRepository.findById(id).orElse(null);
    }

    @PostMapping()
    public Order postOrder(@RequestBody Order order){
        return orderRepository.save(order);
    }

    @PutMapping("/{id}")
    public ResponseBody putOrder(@PathVariable Long id, @RequestBody Order order){

    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id){
        orderRepository.deleteById(id);
    }
}

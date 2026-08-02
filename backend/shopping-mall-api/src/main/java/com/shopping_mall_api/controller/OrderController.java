package com.shopping_mall_api.controller;

import com.shopping_mall_api.entity.order.Order;
import com.shopping_mall_api.repository.order.OrderRepository;
import com.shopping_mall_api.global.constant.TableNames;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/" + TableNames.orderTableName)
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
    public Order getOrder(@PathVariable Integer id){
        return orderRepository.findById(id).orElse(null);
    }

    @PostMapping()
    public Order postOrder(@RequestBody Order order){
        return orderRepository.save(order);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> putOrder(@PathVariable Integer id, @RequestBody Order order){
        boolean exist = orderRepository.existsById(id);

        orderRepository.save(order);

        return (exist) ?
                new ResponseEntity<Order>(order, HttpStatus.OK) :
                new ResponseEntity<Order>(order, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Integer id){
        orderRepository.deleteById(id);
    }
}

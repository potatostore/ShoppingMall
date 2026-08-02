package com.shopping_mall_api.controller;

import com.shopping_mall_api.entity.order.OrderItem;
import com.shopping_mall_api.repository.order.OrderItemRepository;
import com.shopping_mall_api.global.constant.TableNames;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("/" + TableNames.orderItemTableName)
public class OrderItemController {
    private final OrderItemRepository orderDetailRepository;

    public OrderItemController(OrderItemRepository orderDetailRepository){
        this.orderDetailRepository = orderDetailRepository;
    }

    @GetMapping
    public List<OrderItem> getOrderDetail(){
        return orderDetailRepository.findAll();
    }

    @GetMapping("/{id}")
    public OrderItem getOrderDetail(@PathVariable Integer id){
        return orderDetailRepository.findById(id).orElse(null);
    }

    @PostMapping
    public OrderItem postOrderDetail(@RequestBody OrderItem orderItem){
        return orderDetailRepository.save(orderItem);
    }

    @PutMapping("{id}")
    public ResponseEntity<OrderItem> putOrderDetail(@PathVariable Integer id, @RequestBody OrderItem orderItem){
        boolean exist = orderDetailRepository.existsById(id);

        orderDetailRepository.save(orderItem);

        return (exist) ?
                new ResponseEntity<OrderItem>(orderItem, HttpStatus.OK) :
                new ResponseEntity<OrderItem>(orderItem, HttpStatus.CREATED);
    }

    @DeleteMapping("{id}")
    public void deleteOrderDetail(@PathVariable Integer id){
        orderDetailRepository.deleteById(id);
    }
}

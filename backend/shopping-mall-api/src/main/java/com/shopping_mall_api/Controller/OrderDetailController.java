package com.shopping_mall_api.Controller;

import com.shopping_mall_api.Entity.OrderDetail;
import com.shopping_mall_api.Repository.OrderDetailRepository;
import com.shopping_mall_api.TableNames;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("/" + TableNames.orderDetailTableName)
public class OrderDetailController {
    private final OrderDetailRepository orderDetailRepository;

    public OrderDetailController(OrderDetailRepository orderDetailRepository){
        this.orderDetailRepository = orderDetailRepository;
    }

    @GetMapping
    public List<OrderDetail> getOrderDetail(){
        return orderDetailRepository.findAll();
    }

    @GetMapping("/{id}")
    public OrderDetail getOrderDetail(@PathVariable Integer id){
        return orderDetailRepository.findById(id).orElse(null);
    }

    @PostMapping
    public OrderDetail postOrderDetail(@RequestBody OrderDetail orderDetail){
        return orderDetailRepository.save(orderDetail);
    }

    @PutMapping("{id}")
    public ResponseEntity<OrderDetail> putOrderDetail(@PathVariable Integer id, @RequestBody OrderDetail orderDetail){
        boolean exist = orderDetailRepository.existsById(id);

        orderDetailRepository.save(orderDetail);

        return (exist) ?
                new ResponseEntity<OrderDetail>(orderDetail, HttpStatus.OK) :
                new ResponseEntity<OrderDetail>(orderDetail, HttpStatus.CREATED);
    }

    @DeleteMapping("{id}")
    public void deleteOrderDetail(@PathVariable Integer id){
        orderDetailRepository.deleteById(id);
    }
}

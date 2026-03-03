package com.shopping_mall_api.Controller;

import com.shopping_mall_api.Entity.Cart;
import com.shopping_mall_api.Repository.CartRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartRepository cartRepository;

    public CartController(CartRepository cartRepository){
        this.cartRepository = cartRepository;
    }

    @GetMapping
    public List<Cart> getCart(){
        return cartRepository.findAll();
    }

    @GetMapping("{id}")
    public Cart getCart(@PathVariable Integer id){
        return cartRepository.findById(id).orElse(null);
    }

    @PostMapping
    public ResponseEntity<Cart> postCart(@RequestBody Cart cart){

    }

    @PutMapping("/{id}")
    public ResponseEntity<Cart> putCart(@PathVariable Integer id, @RequestBody Cart cart){

    }

    @DeleteMapping("{id}")
    public void deleteCart(@PathVariable Integer id){
        cartRepository.deleteById(id);
    }
}

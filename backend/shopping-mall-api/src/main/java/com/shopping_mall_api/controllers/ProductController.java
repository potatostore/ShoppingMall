package com.shopping_mall_api.controllers;

import com.shopping_mall_api.Entity.Product;
import com.shopping_mall_api.Repository.ProductRepository;
import com.shopping_mall_api.TableNames;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/" + TableNames.productTableName)
public class ProductController {
    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    @GetMapping
    public List<Product> getProduct(){
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Integer id){
        return productRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Product postProduct(@RequestBody Product product){
        return productRepository.save(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> putProduct(@PathVariable Integer id, @RequestBody Product product){
        boolean exist = productRepository.existsById(id);

        productRepository.save(product);

        return (exist) ?
                new ResponseEntity<>(product, HttpStatus.OK):
                new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Integer id){
        productRepository.deleteById(id);
    }
}
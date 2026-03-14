package com.shopping_mall_api.Controller;

import com.shopping_mall_api.Entity.Product;
import com.shopping_mall_api.Repository.ProductRepository;
import com.shopping_mall_api.Service.ProductService;
import com.shopping_mall_api.TableNames;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/" + TableNames.productTableName)
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductService productService;

    public ProductController(ProductRepository productRepository, ProductService productService){
        this.productRepository = productRepository;
        this.productService = productService;
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

    @PostMapping("/addproduct")
    public Product addProduct(@RequestBody Product product){
        ResponseEntity<Product> status = productService.addProduct();
    }

    @PostMapping("/removeproduct")
    public Product removeProduct(@RequestBody Product product){

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
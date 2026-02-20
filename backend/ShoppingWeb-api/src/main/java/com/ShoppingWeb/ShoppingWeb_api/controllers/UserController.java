package com.ShoppingWeb.ShoppingWeb_api.controllers;

import com.ShoppingWeb.ShoppingWeb_api.Entity.User;
import com.ShoppingWeb.ShoppingWeb_api.Repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<User> getUser(){
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Integer id){
        return userRepository.findById(id).orElse(null);
    }

    @PostMapping
    public User postUser(@RequestBody User user){
        return userRepository.save(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> putUser(@PathVariable Integer id, @RequestBody User user){
        boolean exist = userRepository.existsById(id);

        userRepository.save(user);
        return (exist) ?
                new ResponseEntity<User>(user, HttpStatus.OK) :
                new ResponseEntity<User>(user, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id){
        userRepository.deleteById(id);
    }
}

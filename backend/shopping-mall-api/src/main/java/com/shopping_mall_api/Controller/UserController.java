package com.shopping_mall_api.Controller;

import com.shopping_mall_api.DataTransferObject.SignInData;
import com.shopping_mall_api.DataTransferObject.SignUpData;
import com.shopping_mall_api.Entity.User;
import com.shopping_mall_api.Repository.UserRepository;
import com.shopping_mall_api.Service.UserService;
import com.shopping_mall_api.Status.SignInStatus;
import com.shopping_mall_api.TableNames;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/" + TableNames.userTableName)
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;

    public UserController(UserRepository userRepository, UserService userService){
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUser(){
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Integer id){
        return userRepository.findById(id).orElse(null);
    }

    @PostMapping("/signup")
    public ResponseEntity<SignUpData> postUser(@RequestBody SignUpData signUpData){
        SignInStatus status
    }

    @PostMapping("/signin")
    public ResponseEntity<SignInData> signInUser(@RequestBody SignInData signInData){
        HttpStatus.
    }

    @PostMapping("/findbyid")
    public ResponseEntity<>

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

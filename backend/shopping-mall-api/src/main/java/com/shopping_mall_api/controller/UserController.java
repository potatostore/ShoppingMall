package com.shopping_mall_api.controller;

import com.shopping_mall_api.dto.FindIdData;
import com.shopping_mall_api.dto.FindPasswordData;
import com.shopping_mall_api.dto.SignInData;
import com.shopping_mall_api.dto.SignUpData;
import com.shopping_mall_api.entity.user.User;
import com.shopping_mall_api.repository.UserRepository;
import com.shopping_mall_api.service.UserService;
import com.shopping_mall_api.status.SignInStatus;
import com.shopping_mall_api.status.SignUpStatus;
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

    @PostMapping("/signin")
    public ResponseEntity<SignInData> signInUser(@RequestBody SignInData signInData){
        SignInStatus status = userService.signIn(signInData);

        switch(status){
            case SignInStatus.SUCCESS:
                return new ResponseEntity<SignInData>(signInData, HttpStatus.OK);
            case SignInStatus.FAILURE:
                return new ResponseEntity<SignInData>(signInData, HttpStatus.CONFLICT);
            case SignInStatus.LOCKED:
                return new ResponseEntity<SignInData>(signInData, HttpStatus.LOCKED);
            default:
                return new ResponseEntity<SignInData>(signInData, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/signinkakao")
    public ResponseEntity<SignInData> signInKakaoUser(@RequestBody String authCode){
        return null;
    }

    @PostMapping("/signup")
    public ResponseEntity<SignUpData> signUpUser(@RequestBody SignUpData signUpData){
        SignUpStatus status = userService.signUp(signUpData);

        return null;
    }

    @PostMapping("/signupkakao")
    public ResponseEntity<SignUpData> signUpKakaoUser(@RequestBody String authCode){
        return null;
    }

    @PostMapping("/findid")
    public ResponseEntity<FindIdData> FindIdUser(@RequestBody FindIdData findIdData){
        return null;
    }

    @PostMapping("/findpassword")
    public ResponseEntity<FindPasswordData> FindPasswordUser(@RequestBody FindPasswordData findPasswordData){
        return null;
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

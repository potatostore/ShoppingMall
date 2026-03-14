package com.shopping_mall_api.Controller;

import com.shopping_mall_api.DataTransferObject.FindIdData;
import com.shopping_mall_api.DataTransferObject.FindPasswordData;
import com.shopping_mall_api.DataTransferObject.SignInData;
import com.shopping_mall_api.DataTransferObject.SignUpData;
import com.shopping_mall_api.Entity.User;
import com.shopping_mall_api.Repository.UserRepository;
import com.shopping_mall_api.Service.UserService;
import com.shopping_mall_api.Status.FindIdStatus;
import com.shopping_mall_api.Status.FindPasswordStatus;
import com.shopping_mall_api.Status.SignInStatus;
import com.shopping_mall_api.Status.SignUpStatus;
import com.shopping_mall_api.TableNames;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
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

    }

    @PostMapping("/signup")
    public ResponseEntity<SignUpData> signUpUser(@RequestBody SignUpData signUpData){
        SignUpStatus status = userService.signUp(signUpData);

        switch(status){
            case
        }
    }

    @PostMapping("/signupkakao")
    public ResponseEntity<SignUpData> signUpKakaoUser(@RequestBody String authCode){

    }

    @PostMapping("/findid")
    public ResponseEntity<FindIdData> FindIdUser(@RequestBody FindIdData findIdData){
        FindIdStatus status = null;
    }

    @PostMapping("/findpassword")
    public ResponseEntity<FindPasswordData> FindPasswordUser(@RequestBody FindPasswordData findPasswordData){
        FindPasswordStatus status = null;
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

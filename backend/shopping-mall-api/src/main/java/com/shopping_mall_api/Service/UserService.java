package com.shopping_mall_api.Service;

import com.shopping_mall_api.DataTransferObject.SignInData;
import com.shopping_mall_api.Entity.User;
import com.shopping_mall_api.Repository.UserRepository;
import com.shopping_mall_api.Status.SignInStatus;
import com.shopping_mall_api.Status.SignUpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public SignUpStatus signUp(User newUser){
        SignUpStatus status = null;



        return status;
    }

    public SignInStatus signIn(SignInData signInData){

    }

    public
}

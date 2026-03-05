package com.shopping_mall_api.Service;

import com.shopping_mall_api.DataTransferObject.FindIdData;
import com.shopping_mall_api.DataTransferObject.FindPasswordData;
import com.shopping_mall_api.DataTransferObject.SignInData;
import com.shopping_mall_api.DataTransferObject.SignUpData;
import com.shopping_mall_api.Entity.User;
import com.shopping_mall_api.Repository.UserRepository;
import com.shopping_mall_api.Status.FindIdStatus;
import com.shopping_mall_api.Status.FindPasswordStatus;
import com.shopping_mall_api.Status.SignInStatus;
import com.shopping_mall_api.Status.SignUpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public SignUpStatus signUp(SignUpData signUpData){
        SignUpStatus status = null;

        userRepository.findBySignUpId(signUpData);

        return status;
    }

    public SignInStatus signIn(SignInData signInData){
        SignInStatus status = null;

        return status;
    }

    public FindIdStatus findId(FindIdData findIdData){
        FindIdStatus status = null;

        return status;
    }

    public FindPasswordStatus findPassword(FindPasswordData findPasswordData){
        FindPasswordStatus status = null;

        return status;
    }
}

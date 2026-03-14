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
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public SignInStatus signIn(SignInData signInData){
        return userRepository.findBySignInId(signInData.getSignInId())
                .map(user -> {
                    if (!passwordEncoder.matches(signInData.getSignInPassword(), user.getSignInPassword())) {
                        return SignInStatus.FAILURE;
                    }
                    if (user.getLocked()) {
                        return SignInStatus.LOCKED;
                    }
                    return SignInStatus.SUCCESS;
                })
                .orElse(SignInStatus.FAILURE);
    }

    public SignUpStatus signUp(SignUpData signUpData){
        return userRepository.findBySignInId(signUpData.getSignUpId())
                .map(user -> {return SignUpStatus.DUPLICATE_ID;})
                .orElse(userRepository.save(signUpData));
    }

    public FindIdStatus findId(FindIdData findIdData){
        return userRepository.find
    }

    public FindPasswordStatus findPassword(FindPasswordData findPasswordData){
        FindPasswordStatus status = null;

        return status;
    }
}

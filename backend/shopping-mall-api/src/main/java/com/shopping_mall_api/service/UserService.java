package com.shopping_mall_api.service;

import com.shopping_mall_api.repository.user.UserRepository;
import com.shopping_mall_api.status.FindIdStatus;
import com.shopping_mall_api.status.FindPasswordStatus;
import com.shopping_mall_api.status.SignInStatus;
import com.shopping_mall_api.status.SignUpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
                    if (/*user.getLocked()*/ true) {
                        return SignInStatus.LOCKED;
                    }
                    return SignInStatus.SUCCESS;
                })
                .orElse(SignInStatus.FAILURE);
    }

    public SignUpStatus signUp(SignUpData signUpData){
        return null;
    }

    public FindIdStatus findId(FindIdData findIdData){
        return null;
    }

    public FindPasswordStatus findPassword(FindPasswordData findPasswordData){
        FindPasswordStatus status = null;

        return status;
    }
}

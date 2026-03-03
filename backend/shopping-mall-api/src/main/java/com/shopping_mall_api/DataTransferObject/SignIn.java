package com.shopping_mall_api.DataTransferObject;

import lombok.Getter;

@Getter
public class SignIn {
    private String signInId;
    private String signInPassword;

    public void setSignInId(String signInId) {
        this.signInId = signInId;
    }

    public void setSignInPassword(String signInPassword) {
        this.signInPassword = signInPassword;
    }
}

package com.shopping_mall_api.DataTransferObject;

import lombok.Getter;

@Getter
public class SignUpData {
    private String signUpName;
    private String signUpEmail;
    private String signUpId;
    private String signUpPassword;
    private String signUpPhoneNumber;
    private String signUpBirthday;

    public void setSignUpName(String signUpName) {
        this.signUpName = signUpName;
    }

    public void setSignUpEmail(String signUpEmail) {
        this.signUpEmail = signUpEmail;
    }

    public void setSignUpId(String signUpId) {
        this.signUpId = signUpId;
    }

    public void setSignUpPassword(String signUpPassword) {
        this.signUpPassword = signUpPassword;
    }

    public void setSignUpPhoneNumber(String signUpPhoneNumber) {
        this.signUpPhoneNumber = signUpPhoneNumber;
    }

    public void setSignUpBirthday(String signUpBirthday) {
        this.signUpBirthday = signUpBirthday;
    }
}

package com.shopping_mall_api.Status;

public enum SignUpStatus {
    SUCCESS("회원가입 성공"),
    DUPLICATE_ID("이미 존재하는 아이디"),
    INVALID_DATA("입력 데이터가 유효하지 않음");

    private final String message;

    SignUpStatus(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}

package com.shopping_mall_api.Status;

public enum FindIdStatus {
    OK("OK"),
    CANNOTFIND("입력하신 아이디를 찾을 수 없습니다.");

    private final String message;

    FindIdStatus(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}

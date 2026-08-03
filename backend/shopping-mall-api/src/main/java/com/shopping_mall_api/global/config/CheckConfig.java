package com.shopping_mall_api.global.config;

import java.util.Objects;

public class CheckConfig<T>{
    public static <T> void npeCheck(T data, String fieldName){
        Objects.requireNonNull(data, () -> fieldName + " : must not be null");
    }

    public static void npeAndBlankCheck(String data, String fieldName){
        npeCheck(data, fieldName);
        if(data.isBlank()){
            throw new IllegalArgumentException(fieldName + " : must not be blank");
        }
    }

    public static void npeAndNegativeCheck(Long data, String fieldName){
        npeCheck(data, fieldName);
        if(data < 0){
            throw new IllegalArgumentException(fieldName + " : must not be negative");
        }
    }
}

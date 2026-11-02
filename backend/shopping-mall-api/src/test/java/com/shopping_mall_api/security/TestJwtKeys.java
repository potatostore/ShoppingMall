package com.shopping_mall_api.security;

import java.security.SecureRandom;
import java.util.Base64;

// 테스트용 JWT 서명 키를 실행할 때마다 새로 만듦 (소스에 키 문자열을 남기지 않아 시크릿 스캐너에 걸리지 않음)
final class TestJwtKeys {
    private static final SecureRandom RANDOM = new SecureRandom();

    private TestJwtKeys(){
    }

    // HS256에 필요한 256비트 키를 Base64로 (JwtProvider.init이 Base64 디코딩해서 사용)
    static String randomBase64Key(){
        byte[] key = new byte[32];
        RANDOM.nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }
}

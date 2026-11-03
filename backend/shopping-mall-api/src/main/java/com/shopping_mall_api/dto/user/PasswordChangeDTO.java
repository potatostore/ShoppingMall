package com.shopping_mall_api.dto.user;

// [뼈대] 구현 시: @NotBlank, newPassword 길이·조합 규칙 @Pattern, newPassword != currentPassword 검증
public record PasswordChangeDTO(
        String currentPassword,
        String newPassword
) {
}

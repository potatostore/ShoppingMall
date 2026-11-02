package com.shopping_mall_api.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// GET 쿼리스트링 바인딩용 (@ModelAttribute는 setter 또는 생성자로 값을 채움)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FindIdRequestDTO {
    @NotBlank(message = "name must not be blank")
    private String name;

    @NotBlank(message = "phoneNumber must not be blank")
    private String phoneNumber;
}

package com.shopping_mall_api.controller;

import com.shopping_mall_api.dto.cart.CartResponseDTO;
import com.shopping_mall_api.dto.cart.CartUpdateDTO;
import com.shopping_mall_api.global.api.ApiResponse;
import com.shopping_mall_api.global.constant.ApiURLNames;
import com.shopping_mall_api.service.Cart.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping(ApiURLNames.cartURL)
@RequiredArgsConstructor
public class CartController {
    private CartService cartService;

    @GetMapping(ApiURLNames.findCartsURL)
    public ResponseEntity<ApiResponse<List<CartResponseDTO>>> getCarts(){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : get all carts",
                cartService.getCarts()
        ));
    }

    @GetMapping(ApiURLNames.findCartURL)
    public ResponseEntity<ApiResponse<CartResponseDTO>> getCart(@PathVariable Long userId){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : get cart (" + userId + ")",
                cartService.getCart(userId)
        ));
    }

    @PatchMapping(ApiURLNames.updateCartURL)
    public ResponseEntity<ApiResponse<CartResponseDTO>> patchCart(
            @PathVariable Long userId, @Valid @RequestBody CartUpdateDTO cartUpdateDTO){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : patch cart (" + userId + ")",
                cartService.patchCart(userId, cartUpdateDTO)
        ));
    }

    @DeleteMapping(ApiURLNames.deleteCartURL)
    public void deleteCart(@PathVariable Long userId){
        cartService.deleteCart(userId);
    }

    @DeleteMapping(ApiURLNames.deleteCartItemInCartURL)
    public void deleteCartItemInCart(@PathVariable Long userId, @PathVariable Long productId){
        cartService.deleteCartItem(userId, productId);
    }
}

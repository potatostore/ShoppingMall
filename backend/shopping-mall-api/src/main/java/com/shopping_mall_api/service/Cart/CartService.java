package com.shopping_mall_api.service.Cart;

import com.shopping_mall_api.dto.cart.CartResponseDTO;
import com.shopping_mall_api.dto.cart.CartUpdateDTO;
import com.shopping_mall_api.entity.cart.Cart;
import com.shopping_mall_api.entity.product.Product;
import com.shopping_mall_api.global.config.CheckConfig;
import com.shopping_mall_api.global.exception.ErrorCode;
import com.shopping_mall_api.global.exception.NotFoundException;
import com.shopping_mall_api.repository.cart.CartRepository;
import com.shopping_mall_api.repository.product.ProductRepository;
import com.shopping_mall_api.repository.user.UserRepository;
import com.shopping_mall_api.service.product.ProductService;
import com.shopping_mall_api.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    @Transactional
    public CartResponseDTO createCart(Long userId){
        CheckConfig.npeCheck(userId, "userId");

        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND , userId + " : Cannot found user"));

        Cart createCart = Cart.builder()
                .userId(userId)
                .build();

        return new CartResponseDTO(cartRepository.save(createCart));
    }

    public List<CartResponseDTO> getCarts(){
        return cartRepository.findAll().stream()
                .map(CartResponseDTO::new).toList();
    }

    public CartResponseDTO getCart(Long userId){
        CheckConfig.npeCheck(userId, "userId");

        return new CartResponseDTO(cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.CART_NOT_FOUND, "Cannot Found Cart (" + userId + ")")));
    }

    @Transactional
    public CartResponseDTO patchCart(Long userId, CartUpdateDTO cartUpdateDTO){
        CheckConfig.npeCheck(userId, "userId");
        CheckConfig.npeCheck(cartUpdateDTO, "cartUpdateDTO");

        Cart patchCart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.CART_NOT_FOUND, "Cannot Found Cart (" + userId + ")"));

        patchCart.patchCart(cartUpdateDTO);

        return new CartResponseDTO(patchCart);
    }

    public void deleteCart(Long userId){
        CheckConfig.npeCheck(userId, "userId");

        cartRepository.deleteByUserId(userId);
    }

    public void deleteCartItem(Long userId, Long productId){
        CheckConfig.npeCheck(userId, "userId");
        CheckConfig.npeCheck(productId, "productId");

        Cart deleteCart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.CART_NOT_FOUND, "cannot found cart"));

        deleteCart.deleteCartItem(productId);
    }
}

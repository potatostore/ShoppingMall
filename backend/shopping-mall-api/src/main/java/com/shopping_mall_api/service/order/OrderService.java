package com.shopping_mall_api.service.order;

import com.shopping_mall_api.dto.order.OrderResponseDTO;
import com.shopping_mall_api.dto.order.OrderUpdateDTO;
import com.shopping_mall_api.repository.order.OrderRepository;
import com.shopping_mall_api.service.Cart.CartService;
import com.shopping_mall_api.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final CartService cartService;

    public OrderResponseDTO createOrder(Long userId){
        return null;
    }

    public List<OrderResponseDTO> getOrders(){
        return null;
    }

    public List<OrderResponseDTO> getOrdersWithUserId(Long userId){
        return null;
    }

    public OrderResponseDTO getOrder(Long orderId){
        return null;
    }

    public OrderResponseDTO patchOrder(Long orderId, OrderUpdateDTO orderUpdateDTO){
        return null;
    }

    public OrderResponseDTO putOrder(Long orderId, OrderUpdateDTO orderUpdateDTO){
        return null;
    }

    public void deleteOrder(Long orderId){

    }
}

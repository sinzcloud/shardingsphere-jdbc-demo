package com.austin.service;

import com.austin.entity.Order;
import com.austin.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    /**
     * 创建订单（写操作 -> 主库）
     */
    @Transactional
    public Order createOrder(Order order) {
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        order.setStatus(0);  // 待支付

        Order saved = orderRepository.save(order);
        log.info("创建订单成功: orderId={}, userId={}", saved.getOrderId(), saved.getUserId());
        return saved;
    }

    /**
     * 查询用户订单（读操作 -> 从库）
     */
    public List<Order> getUserOrders(Long userId) {
        // ShardingSphere 会自动将读请求路由到从库
        return orderRepository.findByUserId(userId);
    }

    /**
     * 根据订单号查询
     */
    public Order getOrderByNo(String orderNo) {
        return orderRepository.findByOrderNo(orderNo);
    }

    /**
     * 查询用户特定状态的订单
     */
    public List<Order> getUserOrdersByStatus(Long userId, Integer status) {
        return orderRepository.findByUserIdAndStatus(userId, status);
    }

    /**
     * 更新订单状态（写操作 -> 主库）
     */
    @Transactional
    public Order updateOrderStatus(Long orderId, Integer status) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(status);
            order.setUpdateTime(LocalDateTime.now());
            return orderRepository.save(order);
        }
        return null;
    }
}
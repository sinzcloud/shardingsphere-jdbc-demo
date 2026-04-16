package com.austin.controller;

import com.austin.entity.Order;
import com.austin.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }

    @GetMapping("/no/{orderNo}")
    public ResponseEntity<Order> getOrderByNo(@PathVariable String orderNo) {
        Order order = orderService.getOrderByNo(orderNo);
        return order != null ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<List<Order>> getUserOrdersByStatus(
            @PathVariable Long userId,
            @PathVariable Integer status) {
        return ResponseEntity.ok(orderService.getUserOrdersByStatus(userId, status));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam Integer status) {
        Order order = orderService.updateOrderStatus(orderId, status);
        return order != null ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
    }
}
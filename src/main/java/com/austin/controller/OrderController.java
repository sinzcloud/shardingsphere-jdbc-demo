package com.austin.controller;

import com.austin.entity.Order;
import com.austin.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 1. 创建订单
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    // 2. 批量创建订单
    @PostMapping("/batch")
    public ResponseEntity<List<Order>> createOrders(@RequestBody List<Order> orders) {
        return ResponseEntity.ok(orderService.createOrders(orders));
    }

    // 3. 查询用户的所有订单
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    // 4. 根据订单号查询
    @GetMapping("/no/{orderNo}")
    public ResponseEntity<Order> getOrderByNo(@PathVariable String orderNo) {
        Order order = orderService.getOrderByNo(orderNo);
        return order != null ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
    }

    // 5. 查询用户指定状态的订单
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByUserAndStatus(@PathVariable Long userId, @PathVariable Integer status) {
        return ResponseEntity.ok(orderService.getOrdersByUserAndStatus(userId, status));
    }

    // 6. 根据订单ID查询详情
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return order != null ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
    }

    // 7. 更新订单状态
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Map<String, String>> updateStatus(@PathVariable Long orderId, @RequestParam Integer status) {
        int updated = orderService.updateStatus(orderId, status);
        Map<String, String> response = new HashMap<>();
        response.put("result", updated > 0 ? "success" : "order not found");
        return ResponseEntity.ok(response);
    }

    // 8. 更新订单金额
    @PutMapping("/{orderId}")
    public ResponseEntity<Map<String, String>> updateAmount(@PathVariable Long orderId, @RequestBody Map<String, BigDecimal> body) {
        BigDecimal amount = body.get("amount");
        if (amount == null) {
            return ResponseEntity.badRequest().body(Map.of("result", "amount is required"));
        }
        int updated = orderService.updateAmount(orderId, amount);
        Map<String, String> response = new HashMap<>();
        response.put("result", updated > 0 ? "success" : "order not found");
        return ResponseEntity.ok(response);
    }

    // 9. 批量更新用户订单状态
    @PutMapping("/status/batch")
    public ResponseEntity<Map<String, String>> batchUpdateStatus(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        Integer status = (Integer) body.get("status");
        int updated = orderService.updateStatusByUser(userId, status);
        return ResponseEntity.ok(Map.of("result", "updated " + updated + " orders"));
    }

    // 10. 删除订单
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Map<String, String>> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok(Map.of("result", "deleted"));
    }

    // 11. 删除用户所有订单
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Map<String, String>> deleteOrdersByUser(@PathVariable Long userId) {
        orderService.deleteOrdersByUser(userId);
        return ResponseEntity.ok(Map.of("result", "deleted"));
    }

    // 12. 分页查询所有订单
    @GetMapping
    public ResponseEntity<Page<Order>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }

    // 13. 查询金额大于等于指定值的订单
    @GetMapping("/min-amount")
    public ResponseEntity<List<Order>> getOrdersByMinAmount(@RequestParam BigDecimal minAmount) {
        return ResponseEntity.ok(orderService.getOrdersByMinAmount(minAmount));
    }

    // 14. 统计所有订单总金额
    @GetMapping("/total-amount")
    public ResponseEntity<Map<String, BigDecimal>> getTotalAmount() {
        BigDecimal total = orderService.getTotalAmount();
        return ResponseEntity.ok(Map.of("totalAmount", total));
    }

    // 15. 按用户统计订单数量
    @GetMapping("/group-by-user")
    public ResponseEntity<List<Object[]>> getOrderCountGroupByUser() {
        return ResponseEntity.ok(orderService.getOrderCountGroupByUser());
    }
}
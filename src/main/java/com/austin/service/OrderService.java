package com.austin.service;

import com.austin.entity.Order;
import com.austin.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    // 创建订单
    @Transactional
    public Order createOrder(Order order) {
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        if (order.getStatus() == null) {
            order.setStatus(0);
        }
        return orderRepository.save(order);
    }

    // 批量创建订单
    @Transactional
    public List<Order> createOrders(List<Order> orders) {
        orders.forEach(order -> {
            order.setCreateTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            if (order.getStatus() == null) order.setStatus(0);
        });
        return orderRepository.saveAll(orders);
    }

    // 按用户查询
    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    // 按订单号查询
    public Order getOrderByNo(String orderNo) {
        return orderRepository.findByOrderNo(orderNo);
    }

    // 按用户和状态查询
    public List<Order> getOrdersByUserAndStatus(Long userId, Integer status) {
        return orderRepository.findByUserIdAndStatus(userId, status);
    }

    // 按ID查询
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    // 更新订单状态
    @Transactional
    public int updateStatus(Long orderId, Integer status) {
        return orderRepository.updateStatusByOrderId(orderId, status);
    }

    // 批量更新用户订单状态
    @Transactional
    public int updateStatusByUser(Long userId, Integer status) {
        return orderRepository.updateStatusByUserId(userId, status);
    }

    // 更新订单金额
    @Transactional
    public int updateAmount(Long orderId, BigDecimal amount) {
        return orderRepository.updateAmountByOrderId(orderId, amount);
    }

    // 删除订单
    @Transactional
    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    // 删除用户所有订单
    @Transactional
    public void deleteOrdersByUser(Long userId) {
        orderRepository.deleteByUserId(userId);
    }

    // 分页查询所有订单
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAllByOrderByCreateTimeDesc(pageable);
    }

    // 查询金额大于等于指定值的订单
    public List<Order> getOrdersByMinAmount(BigDecimal minAmount) {
        return orderRepository.findByAmountGreaterThanEqual(minAmount);
    }

    @PersistenceContext
    private EntityManager entityManager;

//    // 统计总金额
//    public BigDecimal getTotalAmount() {
//        return orderRepository.getTotalAmount();
//    }

    public BigDecimal getTotalAmount() {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM t_order";
        Query query = entityManager.createNativeQuery(sql);
        List<?> resultList = query.getResultList();
        BigDecimal total = BigDecimal.ZERO;
        for (Object obj : resultList) {
            if (obj != null) {
                total = total.add(new BigDecimal(obj.toString()));
            }
        }
        return total;
    }

    // 统计每个用户的订单数
    public List<Object[]> getOrderCountGroupByUser() {
        return orderRepository.countOrdersByUser();
    }

}
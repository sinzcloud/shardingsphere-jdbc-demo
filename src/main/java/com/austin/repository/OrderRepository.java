package com.austin.repository;

import com.austin.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // 根据用户ID查询（分库键）
    List<Order> findByUserId(Long userId);

    // 根据订单号查询
    Order findByOrderNo(String orderNo);

    // 根据用户ID和状态查询（分库键 + 条件）
    List<Order> findByUserIdAndStatus(Long userId, Integer status);

    // 根据用户ID和时间范围查询（分库键 + 时间范围）
    List<Order> findByUserIdAndCreateTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);

    // 复杂查询示例
    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.status = :status ORDER BY o.createTime DESC")
    List<Order> findUserOrders(@Param("userId") Long userId, @Param("status") Integer status);
}
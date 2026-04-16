package com.austin.repository;

import com.austin.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // 按用户ID查询
    List<Order> findByUserId(Long userId);

    // 按订单号查询
    Order findByOrderNo(String orderNo);

    // 按用户ID和状态查询
    List<Order> findByUserIdAndStatus(Long userId, Integer status);

    // 更新订单状态（根据主键）
    @Modifying
    @Transactional
    @Query("UPDATE Order o SET o.status = :status WHERE o.orderId = :orderId")
    int updateStatusByOrderId(@Param("orderId") Long orderId, @Param("status") Integer status);

    // 批量更新订单状态（根据用户ID）
    @Modifying
    @Transactional
    @Query("UPDATE Order o SET o.status = :status WHERE o.userId = :userId")
    int updateStatusByUserId(@Param("userId") Long userId, @Param("status") Integer status);

    // 更新订单金额（根据主键）
    @Modifying
    @Transactional
    @Query("UPDATE Order o SET o.amount = :amount, o.updateTime = CURRENT_TIMESTAMP WHERE o.orderId = :orderId")
    int updateAmountByOrderId(@Param("orderId") Long orderId, @Param("amount") BigDecimal amount);

    // 删除用户的所有订单
    @Modifying
    @Transactional
    void deleteByUserId(Long userId);

    // 分页查询所有订单（按创建时间倒序）
    Page<Order> findAllByOrderByCreateTimeDesc(Pageable pageable);

    // 查询金额大于等于指定值的订单
    List<Order> findByAmountGreaterThanEqual(BigDecimal amount);

    // 统计所有订单总金额（跨分片聚合，ShardingSphere 支持）
    @Query("SELECT COALESCE(SUM(o.amount), 0) FROM Order o")
    BigDecimal getTotalAmount();

    // 按用户统计订单数量（返回每个用户的订单数）
    @Query("SELECT o.userId, COUNT(o) FROM Order o GROUP BY o.userId")
    List<Object[]> countOrdersByUser();
}
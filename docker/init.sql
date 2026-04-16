-- 创建数据库
CREATE DATABASE IF NOT EXISTS sharding_db;
USE sharding_db;

-- 用户表（不分表）
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL,
    `name` VARCHAR(100),
    `age` INT,
    PRIMARY KEY (`id`)
);

-- 订单分表 t_order_0
CREATE TABLE IF NOT EXISTS `t_order_0` (
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号',
    `amount` DECIMAL(10,2) COMMENT '订单金额',
    `status` INT COMMENT '订单状态',
    `create_time` DATETIME COMMENT '创建时间',
    `update_time` DATETIME COMMENT '更新时间',
    PRIMARY KEY (`order_id`),
    INDEX idx_user_id (`user_id`),
    INDEX idx_order_no (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表分片0';

-- 订单分表 t_order_1
CREATE TABLE IF NOT EXISTS `t_order_1` (
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号',
    `amount` DECIMAL(10,2) COMMENT '订单金额',
    `status` INT COMMENT '订单状态',
    `create_time` DATETIME COMMENT '创建时间',
    `update_time` DATETIME COMMENT '更新时间',
    PRIMARY KEY (`order_id`),
    INDEX idx_user_id (`user_id`),
    INDEX idx_order_no (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表分片1';

-- 创建复制用户（从库使用）
CREATE USER IF NOT EXISTS 'repl'@'%' IDENTIFIED WITH mysql_native_password BY 'replpass';
GRANT REPLICATION SLAVE ON *.* TO 'repl'@'%';
FLUSH PRIVILEGES;
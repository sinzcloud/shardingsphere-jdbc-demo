# ShardingSphere-JDBC 分库分表 + 读写分离测试环境

## 项目简介

本项目提供一套完整的 **MySQL 主从集群** 环境，用于测试 **ShardingSphere-JDBC** 的分库分表和读写分离功能。通过 Docker Compose 一键启动 4 个 MySQL 容器：

- `ds0-master` + `ds0-slave`（分片 0 的主从）
- `ds1-master` + `ds1-slave`（分片 1 的主从）

每个主库会自动初始化 `sharding_db` 数据库，并创建分表 `t_order_0` / `t_order_1` 以及 `user` 表。从库通过 GTID 自动同步主库数据。

> 适用于学习 ShardingSphere 核心概念、验证分片算法、测试主从同步延迟等场景，无需手动搭建复杂数据库集群。

---

## 技术栈

| 组件                  | 版本                           |
| --------------------- | ------------------------------ |
| MySQL                 | 8.0                            |
| Docker / Docker Compose | 20.10+                       |
| ShardingSphere-JDBC   | 5.5.2（推荐）                  |
| Spring Boot           | 3.1.3（可选，兼容任意 Java 应用）|

---

## 环境要求

- 安装 Docker 和 Docker Compose
- 宿主机可用端口：`3306, 3307, 3308, 3309`
- （可选）JDK 17+ 用于运行测试应用

---

## 快速开始

### 1. 下载配置文件

将以下两个文件保存到同一目录（如 `sharding-cluster/`）：

- `docker-compose.yml`
- `init.sql`

### 2. 启动集群

```bash
cd sharding-cluster
docker-compose up -d
```

首次启动会自动执行 `init.sql`，创建数据库、表及复制用户。等待约 30 秒后集群就绪。

### 3. 检查集群状态

#### Linux / macOS / Git Bash:
```bash
# 查看容器状态
docker-compose ps

# 验证主从复制（两个字段应为 Yes）
docker exec ds0-slave mysql -uroot -proot123 -e "SHOW SLAVE STATUS\G" | grep -E "Slave_IO_Running|Slave_SQL_Running"
docker exec ds1-slave mysql -uroot -proot123 -e "SHOW SLAVE STATUS\G" | grep -E "Slave_IO_Running|Slave_SQL_Running"
```

#### Windows PowerShell:
```powershell
docker-compose ps
docker exec ds0-slave mysql -uroot -proot123 -e "SHOW SLAVE STATUS\G" | Select-String "Slave_IO_Running|Slave_SQL_Running"
docker exec ds1-slave mysql -uroot -proot123 -e "SHOW SLAVE STATUS\G" | Select-String "Slave_IO_Running|Slave_SQL_Running"
```

预期输出：
```
Slave_IO_Running: Yes
Slave_SQL_Running: Yes
```

### 4. 连接信息

| 数据源名称 | 主机       | 端口 | 数据库       | 用户名 | 密码     | 角色 |
| ---------- | ---------- | ---- | ------------ | ------ | -------- | ---- |
| ds0-master | localhost  | 3306 | sharding_db  | root   | root123  | 写   |
| ds0-slave  | localhost  | 3307 | sharding_db  | root   | root123  | 读   |
| ds1-master | localhost  | 3308 | sharding_db  | root   | root123  | 写   |
| ds1-slave  | localhost  | 3309 | sharding_db  | root   | root123  | 读   |

---

## 在 ShardingSphere-JDBC 中使用

您需要在应用程序中配置分片规则和读写分离规则。简要配置思路如下（不贴具体代码，请参考官方文档）：

- **分库键**：`user_id`（取模 2，路由到 ds0 或 ds1）
- **分表键**：`order_id`（取模 2，路由到 `t_order_0` 或 `t_order_1`）
- **读写分离**：为每个分片（ds0/ds1）配置主库（master）和从库（slave），写操作使用主库，读操作使用从库。

详细配置请参考 ShardingSphere 官方文档或项目附带的 `application.yml` 示例（已放置在 `src/main/resources` 中）。

---

## 测试 API（可选）

如果您已启动配套的 Spring Boot 应用（基于本项目的代码），可以使用以下 curl 命令快速测试：

```bash
# 创建订单（写 → 主库）
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"orderId":1001,"userId":1,"orderNo":"ORD001","amount":99.99}'

# 查询用户订单（读 → 从库）
curl http://localhost:8080/api/orders/user/1
```

Postman 测试集合可导入项目提供的 `sharding-test.postman_collection.json` 文件。

---

## 常见问题

### 1. 主从复制状态异常（Slave_IO_Running 或 Slave_SQL_Running 为 No）

- 检查 `setup-ds0-replication` 和 `setup-ds1-replication` 容器的日志：
  ```bash
  docker logs setup-ds0-replication
  docker logs setup-ds1-replication
  ```
- 确认 `init.sql` 中已成功创建复制用户 `repl`。
- 重启集群：`docker-compose down -v && docker-compose up -d`

### 2. 应用程序连接 MySQL 失败

- 确保宿主机端口未被占用：`netstat -an | grep 3306`（Windows 用 `netstat -an | findstr 3306`）
- 检查防火墙是否允许端口访问。
- 确认 MySQL 容器已完全启动（健康检查通过）。

### 3. 如何重置环境？

```bash
docker-compose down -v   # 删除所有容器和数据卷，重新开始
```

---

## 项目文件说明

- `docker-compose.yml`：定义 4 个 MySQL 容器及主从自动配置服务。
- `init.sql`：初始化 SQL，创建数据库、表及复制用户。
- （可选）`src/`：配套的 Spring Boot 应用代码，演示如何集成 ShardingSphere-JDBC。

---

## 许可证

本项目仅供学习测试使用。
```
-- 设备管理系统数据库建表脚本
-- Database: equipment_management

CREATE DATABASE IF NOT EXISTS equipment_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE equipment_management;

-- 1. 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(200) NOT NULL COMMENT '密码（BCrypt）',
    real_name VARCHAR(50) COMMENT '真实姓名',
    role VARCHAR(20) DEFAULT 'user' COMMENT '角色：admin/user',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT = '系统用户表';

-- 1.5 设备分类表
CREATE TABLE IF NOT EXISTS device_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL COMMENT '分类名称',
    description VARCHAR(500) COMMENT '分类描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT = '设备分类表';

-- 2. 设备表
CREATE TABLE IF NOT EXISTS device (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_code VARCHAR(50) NOT NULL UNIQUE COMMENT '设备编码',
    device_name VARCHAR(100) NOT NULL COMMENT '设备名称',
    category_id BIGINT COMMENT '分类ID',
    supplier VARCHAR(100) COMMENT '供应商',
    model VARCHAR(100) COMMENT '规格型号',
    unit_price DECIMAL(10,2) COMMENT '单价',
    manufacturer VARCHAR(100) COMMENT '生产厂家',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT = '设备信息表';

-- 3. 库存表
CREATE TABLE IF NOT EXISTS inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id BIGINT NOT NULL COMMENT '设备ID',
    quantity INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    status VARCHAR(20) DEFAULT '正常' COMMENT '设备状态',
    warning_value INT DEFAULT 5 COMMENT '库存预警值',
    location VARCHAR(200) COMMENT '存放位置',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_device_id (device_id)
) COMMENT = '库存表';

-- 4. 出入库记录表
CREATE TABLE IF NOT EXISTS stock_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id BIGINT NOT NULL COMMENT '设备ID',
    operation_type VARCHAR(20) NOT NULL COMMENT '操作类型：IN/OUT/RETURN',
    quantity INT NOT NULL COMMENT '数量',
    receiver VARCHAR(50) COMMENT '领用人',
    source_place VARCHAR(200) COMMENT '来源',
    target_place VARCHAR(200) COMMENT '去处',
    operator_name VARCHAR(50) COMMENT '经手人',
    operation_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    KEY idx_device_id (device_id),
    KEY idx_operation_time (operation_time)
) COMMENT = '出入库记录表';

-- 5. 库存盘点表
CREATE TABLE IF NOT EXISTS inventory_check (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id BIGINT NOT NULL COMMENT '设备ID',
    system_quantity INT COMMENT '系统库存',
    actual_quantity INT COMMENT '实际库存',
    difference_quantity INT COMMENT '差异数量',
    checker VARCHAR(50) COMMENT '盘点人',
    check_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '盘点时间',
    KEY idx_device_id (device_id)
) COMMENT = '库存盘点表';

-- 6. 采购订单表
CREATE TABLE IF NOT EXISTS purchase_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id BIGINT NOT NULL COMMENT '设备ID',
    purchase_quantity INT NOT NULL COMMENT '采购数量',
    purchase_price DECIMAL(10,2) NOT NULL COMMENT '采购单价',
    total_amount DECIMAL(12,2) COMMENT '采购总金额',
    purchaser VARCHAR(50) COMMENT '采购员',
    purchase_date DATE COMMENT '采购日期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_device_id (device_id),
    KEY idx_purchase_date (purchase_date)
) COMMENT = '采购订单表';

-- 7. 操作日志表
CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) COMMENT '用户名',
    operation_content VARCHAR(200) COMMENT '操作内容',
    request_method VARCHAR(10) COMMENT '请求方法',
    request_url VARCHAR(500) COMMENT '请求路径',
    ip VARCHAR(50) COMMENT '操作IP',
    result TINYINT DEFAULT 1 COMMENT '0失败 1成功',
    operation_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    KEY idx_operation_time (operation_time)
) COMMENT = '操作日志表';

-- 8. 设备报损/报废表
CREATE TABLE IF NOT EXISTS device_scrap (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id BIGINT NOT NULL COMMENT '设备ID',
    scrap_quantity INT NOT NULL COMMENT '报损数量',
    scrap_reason VARCHAR(500) NOT NULL COMMENT '报损原因',
    scrap_type VARCHAR(20) NOT NULL COMMENT '报损类型：SCRAP(报废)/REPAIR(报修)/DAMAGE(损坏)',
    scrap_date DATE NOT NULL COMMENT '报损日期',
    applicant VARCHAR(50) COMMENT '申请人',
    approver VARCHAR(50) COMMENT '审批人',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING(待审批)/APPROVED(已审批)/REJECTED(已驳回)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_device_id (device_id),
    KEY idx_scrap_date (scrap_date)
) COMMENT = '设备报损报废表';

-- ==================== 索引 ====================
ALTER TABLE device ADD INDEX idx_device_code (device_code);
ALTER TABLE device ADD INDEX idx_device_name (device_name);

-- ==================== 外键约束 ====================
ALTER TABLE device ADD CONSTRAINT fk_device_category
    FOREIGN KEY (category_id) REFERENCES device_category(id);
ALTER TABLE inventory ADD CONSTRAINT fk_inventory_device
    FOREIGN KEY (device_id) REFERENCES device(id);
ALTER TABLE stock_record ADD CONSTRAINT fk_stock_device
    FOREIGN KEY (device_id) REFERENCES device(id);
ALTER TABLE purchase_order ADD CONSTRAINT fk_order_device
    FOREIGN KEY (device_id) REFERENCES device(id);
ALTER TABLE inventory_check ADD CONSTRAINT fk_check_device
    FOREIGN KEY (device_id) REFERENCES device(id);
ALTER TABLE device_scrap ADD CONSTRAINT fk_scrap_device
    FOREIGN KEY (device_id) REFERENCES device(id);

-- ==================== 初始化数据 ====================

-- 默认管理员账号（密码: admin123，BCrypt 加密）
INSERT INTO sys_user (username, password, real_name, role) VALUES
('admin', '$2a$10$D0CEVMqv8JT6eeYKSjudE.qxyGm9p8UPHZX42cjlmD324M9OiontC', '系统管理员', 'admin');

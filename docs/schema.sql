CREATE DATABASE IF NOT EXISTS outdoor_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE outdoor_db;

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(64),
    avatar VARCHAR(255),
    role TINYINT DEFAULT 0 COMMENT '0用户 1领队 2管理员',
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS guide (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    real_name VARCHAR(64),
    id_card VARCHAR(20),
    intro TEXT,
    tags VARCHAR(255),
    price DECIMAL(10,2) DEFAULT 0.00,
    city VARCHAR(64),
    status TINYINT DEFAULT 0 COMMENT '0待审核 1通过 2拒绝',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_guide_user (user_id),
    KEY idx_guide_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS activity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    guide_id BIGINT NOT NULL,
    title VARCHAR(128) NOT NULL,
    type VARCHAR(64),
    difficulty TINYINT DEFAULT 1,
    destination VARCHAR(128),
    gather_point VARCHAR(255),
    start_time DATETIME,
    end_time DATETIME,
    price DECIMAL(10,2) DEFAULT 0.00,
    max_num INT DEFAULT 0,
    current_num INT DEFAULT 0,
    cover VARCHAR(255),
    content TEXT,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    KEY idx_activity_guide (guide_id),
    KEY idx_activity_status (status),
    KEY idx_activity_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS booking (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    activity_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    guide_id BIGINT NOT NULL,
    book_date DATE,
    num INT DEFAULT 1,
    total_price DECIMAL(10,2) DEFAULT 0.00,
    status TINYINT DEFAULT 0 COMMENT '0待支付 1已支付 2已取消 3已完成',
    pay_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    KEY idx_booking_user (user_id),
    KEY idx_booking_guide (guide_id),
    KEY idx_booking_activity (activity_id),
    KEY idx_booking_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS review (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT,
    activity_id BIGINT,
    user_id BIGINT NOT NULL,
    guide_id BIGINT,
    rating TINYINT DEFAULT 5,
    content TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_review_booking (booking_id),
    KEY idx_review_activity (activity_id),
    KEY idx_review_guide (guide_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    from_user_id BIGINT,
    to_user_id BIGINT,
    content TEXT,
    is_read TINYINT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    KEY idx_message_to (to_user_id, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

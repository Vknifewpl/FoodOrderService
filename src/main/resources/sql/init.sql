-- 创建数据库
CREATE DATABASE IF NOT EXISTS food_order DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE food_order;

-- 1. 用户表（user）
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码(加密存储)',
  `role` TINYINT NOT NULL DEFAULT 0 COMMENT '角色:0-普通用户,1-管理员',
  `token` VARCHAR(500) DEFAULT NULL COMMENT '登录Token',
  `is_new_user` TINYINT NOT NULL DEFAULT 1 COMMENT '是否新用户:1-是,0-否',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除:0-否,1-是',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `idx_username` (`username`),
  INDEX `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 菜品分类表（food_category）
CREATE TABLE IF NOT EXISTS `food_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `sort` INT NOT NULL DEFAULT 0 COMMENT '排序号',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除:0-否,1-是',
  PRIMARY KEY (`id`),
  INDEX `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜品分类表';

-- 3. 菜品表（food）
CREATE TABLE IF NOT EXISTS `food` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '菜品ID',
  `name` VARCHAR(100) NOT NULL COMMENT '菜品名称',
  `category_id` BIGINT NOT NULL COMMENT '分类ID',
  `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
  `description` TEXT COMMENT '描述',
  `image` VARCHAR(255) COMMENT '图片路径',
  `order_count` INT NOT NULL DEFAULT 0 COMMENT '点餐次数',
  `praise_count` INT NOT NULL DEFAULT 0 COMMENT '好评数量',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除:0-否,1-是',
  PRIMARY KEY (`id`),
  INDEX `idx_category_id` (`category_id`),
  INDEX `idx_order_count` (`order_count` DESC),
  INDEX `idx_praise_count` (`praise_count` DESC),
  INDEX `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜品表';

-- 4. 订单表（orders）
CREATE TABLE IF NOT EXISTS `orders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态:0-待支付,1-已支付,2-已完成',
  `order_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
  `complete_time` DATETIME DEFAULT NULL COMMENT '完成时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除:0-否,1-是',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `idx_order_no` (`order_no`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_order_time` (`order_time` DESC),
  INDEX `idx_user_status` (`user_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 5. 订单明细表（order_item）
CREATE TABLE IF NOT EXISTS `order_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号',
  `food_id` BIGINT NOT NULL COMMENT '菜品ID',
  `food_name` VARCHAR(100) NOT NULL COMMENT '菜品名称(冗余)',
  `food_image` VARCHAR(255) COMMENT '菜品图片(冗余)',
  `price` DECIMAL(10,2) NOT NULL COMMENT '单价',
  `quantity` INT NOT NULL COMMENT '数量',
  `subtotal` DECIMAL(10,2) NOT NULL COMMENT '小计金额',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_order_id` (`order_id`),
  INDEX `idx_order_no` (`order_no`),
  INDEX `idx_food_id` (`food_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- 6. 收藏表（collection）
CREATE TABLE IF NOT EXISTS `collection` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `food_id` BIGINT NOT NULL COMMENT '菜品ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `idx_user_food` (`user_id`, `food_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_food_id` (`food_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- 7. 评论表（comment）
CREATE TABLE IF NOT EXISTS `comment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `food_id` BIGINT NOT NULL COMMENT '菜品ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `rating` TINYINT NOT NULL DEFAULT 5 COMMENT '评分:1-5',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除:0-否,1-是',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_food_id` (`food_id`),
  INDEX `idx_order_id` (`order_id`),
  INDEX `idx_create_time` (`create_time` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 8. 用户偏好表（user_preference）
CREATE TABLE IF NOT EXISTS `user_preference` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '偏好ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `food_id` BIGINT NOT NULL COMMENT '菜品ID',
  `preference_score` DECIMAL(5,2) NOT NULL DEFAULT 0 COMMENT '偏好得分',
  `order_count` INT NOT NULL DEFAULT 0 COMMENT '点餐次数',
  `is_collected` TINYINT NOT NULL DEFAULT 0 COMMENT '是否收藏:0-否,1-是',
  `comment_rating` TINYINT DEFAULT NULL COMMENT '评论评分',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `idx_user_food` (`user_id`, `food_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_food_id` (`food_id`),
  INDEX `idx_preference_score` (`preference_score` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户偏好表(协同过滤算法)';

-- 插入默认管理员账号 (密码: admin123，使用MD5加密)
INSERT INTO `user` (`username`, `password`, `role`, `is_new_user`) VALUES 
('admin', 'e10adc3949ba59abbe56e057f20f883e', 1, 0);

-- 插入默认分类数据
INSERT INTO `food_category` (`name`, `sort`) VALUES 
('凉菜', 1),
('热菜', 2),
('主食', 3),
('汤类', 4),
('饮品', 5),
('甜点', 6);


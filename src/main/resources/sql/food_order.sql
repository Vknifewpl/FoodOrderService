/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44)
 Source Host           : localhost:3306
 Source Schema         : food_order

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44)
 File Encoding         : 65001

 Date: 05/03/2026 01:06:01
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for collection
-- ----------------------------
DROP TABLE IF EXISTS `collection`;
CREATE TABLE `collection`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `food_id` bigint NOT NULL COMMENT '菜品ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_user_food`(`user_id` ASC, `food_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_food_id`(`food_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '收藏表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of collection
-- ----------------------------

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `food_id` bigint NOT NULL COMMENT '菜品ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `rating` tinyint NOT NULL DEFAULT 5 COMMENT '评分:1-5',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除:0-否,1-是',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_food_id`(`food_id` ASC) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of comment
-- ----------------------------

-- ----------------------------
-- Table structure for food
-- ----------------------------
DROP TABLE IF EXISTS `food`;
CREATE TABLE `food`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜品ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜品名称',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `price` decimal(10, 2) NOT NULL COMMENT '价格',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '描述',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片路径',
  `order_count` int NOT NULL DEFAULT 0 COMMENT '点餐次数',
  `praise_count` int NOT NULL DEFAULT 0 COMMENT '好评数量',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除:0-否,1-是',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_order_count`(`order_count` ASC) USING BTREE,
  INDEX `idx_praise_count`(`praise_count` ASC) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE,
  FULLTEXT INDEX `idx_name_fulltext`(`name`) WITH PARSER `ngram`
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜品表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of food
-- ----------------------------
INSERT INTO `food` VALUES (1, '宫保鸡丁', 2, 38.00, '经典川菜，酸辣适口，鸡肉鲜嫩。', '/images/food1.jpg', 120, 115, '2026-03-04 20:27:52', '2026-03-04 20:27:52', 0);
INSERT INTO `food` VALUES (2, '红烧肉', 2, 48.00, '肥而不腻，入口即化，口味浓郁。', '/images/food2.jpg', 200, 190, '2026-03-04 20:27:53', '2026-03-04 20:27:53', 0);
INSERT INTO `food` VALUES (3, '鱼香肉丝', 2, 32.00, '色泽红亮，咸甜酸辣，五味俱全。', '/images/food3.jpg', 150, 140, '2026-03-04 20:27:53', '2026-03-04 20:27:53', 0);
INSERT INTO `food` VALUES (4, '麻婆豆腐', 2, 22.00, '麻、辣、鲜、香、烫、嫩、酥。', '/images/food4.jpg', 180, 175, '2026-03-04 20:27:53', '2026-03-04 20:27:53', 0);
INSERT INTO `food` VALUES (5, '扬州炒饭', 1, 18.00, '颗粒分明，色彩丰富，营养均衡。', '/images/food5.jpg', 300, 280, '2026-03-04 20:27:53', '2026-03-04 20:27:53', 0);
INSERT INTO `food` VALUES (6, '兰州拉面', 1, 15.00, '汤清、萝卜白、辣油红、香菜绿、面黄。', '/images/food6.jpg', 250, 230, '2026-03-04 20:27:53', '2026-03-04 20:27:53', 0);
INSERT INTO `food` VALUES (7, '炸酱面', 1, 20.00, '地道老北京风味，酱香浓郁。', '/images/food7.jpg', 140, 130, '2026-03-04 20:27:53', '2026-03-04 20:27:53', 0);
INSERT INTO `food` VALUES (8, '拍黄瓜', 3, 12.00, '清脆爽口，酸辣解腻。', '/images/food8.jpg', 110, 105, '2026-03-04 20:27:53', '2026-03-04 20:27:53', 0);
INSERT INTO `food` VALUES (9, '夫妻肺片', 3, 35.00, '麻辣浓香，口感丰富多变。', '/images/food9.jpg', 90, 85, '2026-03-04 20:27:53', '2026-03-04 20:27:53', 0);
INSERT INTO `food` VALUES (10, '西红柿鸡蛋汤', 4, 15.00, '清新可口，简单大方的家庭美味。', '/images/food10.jpg', 130, 125, '2026-03-04 20:27:53', '2026-03-04 20:27:53', 0);
INSERT INTO `food` VALUES (11, '大骨头汤', 4, 28.00, '慢火细熬，浓郁香滑。', '/images/food11.jpg', 80, 75, '2026-03-04 20:27:53', '2026-03-04 20:27:53', 0);
INSERT INTO `food` VALUES (12, '杨枝甘露', 5, 22.00, '果香浓郁，清甜解馋。', '/images/food12.jpg', 160, 155, '2026-03-04 20:27:53', '2026-03-04 20:27:53', 0);
INSERT INTO `food` VALUES (13, '珍珠奶茶', 5, 12.00, '茶香奶醇，珍珠Q弹。', '/images/food13.jpg', 210, 200, '2026-03-04 20:27:53', '2026-03-04 20:27:53', 0);
INSERT INTO `food` VALUES (14, '酸梅汤', 5, 8.00, '传统解暑良药，生津止渴。', '/images/food14.jpg', 190, 185, '2026-03-04 20:27:53', '2026-03-04 20:27:53', 0);
INSERT INTO `food` VALUES (15, '回锅肉', 2, 35.00, '川菜之王，香辣爽口。', '/images/food15.jpg', 170, 165, '2026-03-04 20:27:53', '2026-03-04 20:27:53', 0);
-- 精选小吃 (category_id=6)
INSERT INTO `food` VALUES (16, '炸鸡柳', 6, 15.00, '外酥里嫩，金黄香脆，蘸酱更美味。', '/images/food16.png', 95, 88, '2026-03-05 01:00:00', '2026-03-05 01:00:00', 0);
INSERT INTO `food` VALUES (17, '煎饺', 6, 18.00, '底部焦脆金黄，馅料鲜美多汁。', '/images/food17.png', 110, 102, '2026-03-05 01:00:00', '2026-03-05 01:00:00', 0);
INSERT INTO `food` VALUES (18, '烤肠', 6, 8.00, '炭火慢烤，表皮焦香，肉质紧实。', '/images/food18.png', 200, 185, '2026-03-05 01:00:00', '2026-03-05 01:00:00', 0);
INSERT INTO `food` VALUES (19, '薯条', 6, 12.00, '现炸金黄酥脆，撒盐调味，经典美味。', '/images/food19.png', 180, 170, '2026-03-05 01:00:00', '2026-03-05 01:00:00', 0);
INSERT INTO `food` VALUES (20, '鸡米花', 6, 16.00, '一口一个，外酥里嫩，香辣可口。', '/images/food20.png', 150, 140, '2026-03-05 01:00:00', '2026-03-05 01:00:00', 0);
-- 解渴饮料 (category_id=7)
INSERT INTO `food` VALUES (21, '冰镇西瓜汁', 7, 10.00, '鲜榨西瓜，冰凉解暑，甜蜜爽口。', '/images/food21.png', 160, 155, '2026-03-05 01:00:00', '2026-03-05 01:00:00', 0);
INSERT INTO `food` VALUES (22, '柠檬蜂蜜水', 7, 12.00, '蜂蜜与柠檬的完美搭配，酸甜开胃。', '/images/food22.png', 130, 125, '2026-03-05 01:00:00', '2026-03-05 01:00:00', 0);
INSERT INTO `food` VALUES (23, '芒果冰沙', 7, 18.00, '浓郁芒果果肉打制，细腻顺滑，热带风情。', '/images/food23.png', 140, 135, '2026-03-05 01:00:00', '2026-03-05 01:00:00', 0);
INSERT INTO `food` VALUES (24, '椰汁', 7, 10.00, '天然椰子鲜榨，清甜解渴，营养丰富。', '/images/food24.png', 120, 115, '2026-03-05 01:00:00', '2026-03-05 01:00:00', 0);
INSERT INTO `food` VALUES (25, '百香果气泡水', 7, 15.00, '百香果搭配气泡水，酸甜气泡在舌尖跳跃。', '/images/food25.png', 100, 95, '2026-03-05 01:00:00', '2026-03-05 01:00:00', 0);
-- 无辣不欢 (category_id=8)
INSERT INTO `food` VALUES (26, '水煮鱼', 8, 58.00, '鱼片嫩滑，红油翻滚，麻辣鲜香。', '/images/food26.png', 85, 80, '2026-03-05 01:00:00', '2026-03-05 01:00:00', 0);
INSERT INTO `food` VALUES (27, '辣子鸡', 8, 42.00, '鸡丁酥脆，干辣椒满盘，越嚼越香。', '/images/food27.png', 95, 90, '2026-03-05 01:00:00', '2026-03-05 01:00:00', 0);
INSERT INTO `food` VALUES (28, '麻辣烫', 8, 25.00, '各色食材涮煮，麻辣鲜香，一锅满足。', '/images/food28.png', 220, 210, '2026-03-05 01:00:00', '2026-03-05 01:00:00', 0);
INSERT INTO `food` VALUES (29, '剁椒鱼头', 8, 52.00, '鲜嫩鱼头配红彤彤剁椒，鲜辣开胃。', '/images/food29.png', 75, 70, '2026-03-05 01:00:00', '2026-03-05 01:00:00', 0);
INSERT INTO `food` VALUES (30, '酸辣粉', 8, 15.00, '红薯粉Q弹爽滑，酸辣过瘾，经典街头小吃。', '/images/food30.png', 250, 240, '2026-03-05 01:00:00', '2026-03-05 01:00:00', 0);

-- ----------------------------
-- Table structure for food_category
-- ----------------------------
DROP TABLE IF EXISTS `food_category`;
CREATE TABLE `food_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除:0-否,1-是',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sort`(`sort` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜品分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of food_category
-- ----------------------------
INSERT INTO `food_category` VALUES (1, '精选主食', 1, '2026-03-04 20:27:53', '2026-03-04 20:27:53', 0);
INSERT INTO `food_category` VALUES (2, '招牌热菜', 2, '2026-03-04 20:27:53', '2026-03-04 20:27:53', 0);
INSERT INTO `food_category` VALUES (3, '爽口凉菜', 3, '2026-03-04 20:27:53', '2026-03-04 20:27:53', 0);
INSERT INTO `food_category` VALUES (4, '养生汤羹', 4, '2026-03-04 20:27:53', '2026-03-04 20:27:53', 0);
INSERT INTO `food_category` VALUES (5, '甜品饮品', 5, '2026-03-04 20:27:53', '2026-03-04 20:27:53', 0);
INSERT INTO `food_category` VALUES (6, '精选小吃', 0, '2026-03-05 00:19:53', '2026-03-05 00:19:54', 0);
INSERT INTO `food_category` VALUES (7, '解渴饮料', 0, '2026-03-05 00:20:23', '2026-03-05 00:20:23', 0);
INSERT INTO `food_category` VALUES (8, '无辣不欢', 0, '2026-03-05 00:20:54', '2026-03-05 00:20:54', 0);

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单编号',
  `food_id` bigint NOT NULL COMMENT '菜品ID',
  `food_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜品名称(冗余)',
  `food_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '菜品图片(冗余)',
  `price` decimal(10, 2) NOT NULL COMMENT '单价',
  `quantity` int NOT NULL COMMENT '数量',
  `subtotal` decimal(10, 2) NOT NULL COMMENT '小计金额',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_food_id`(`food_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单明细表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of order_item
-- ----------------------------

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总金额',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '订单状态:0-待支付,1-已支付,2-已完成',
  `order_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `complete_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除:0-否,1-是',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_order_time`(`order_time` ASC) USING BTREE,
  INDEX `idx_user_status`(`user_id` ASC, `status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of orders
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码(加密存储)',
  `role` tinyint NOT NULL DEFAULT 0 COMMENT '角色:0-普通用户,1-管理员',
  `token` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '登录Token',
  `is_new_user` tinyint NOT NULL DEFAULT 1 COMMENT '是否新用户:1-是,0-否',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除:0-否,1-是',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_role`(`role` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'user', 'e10adc3949ba59abbe56e057f20f883e', 0, 'eyJhbGciOiJIUzUxMiJ9.eyJyb2xlIjowLCJleHAiOjE3NzI3MjgzMzgsInVzZXJJZCI6MSwiaWF0IjoxNzcyNjQxOTM4LCJ1c2VybmFtZSI6InVzZXIifQ.HhjZ8tvCK1i3oxRjSzhcvdLyKTjkPc0pE4z7SJhEM9ppS6ATzuLKU-2HOpp_7-KAQN4_ErGTfbV9gNcHs9Q4iQ', 1, '2026-02-25 18:53:26', '2026-03-05 00:32:18', 0);
INSERT INTO `user` VALUES (2, 'admin', 'e10adc3949ba59abbe56e057f20f883e', 1, 'eyJhbGciOiJIUzUxMiJ9.eyJyb2xlIjoxLCJleHAiOjE3NzI3MjgwNjYsInVzZXJJZCI6MiwiaWF0IjoxNzcyNjQxNjY2LCJ1c2VybmFtZSI6ImFkbWluIn0.vkKDloznvdCphQITXxgIPSGLXpmZid1QK8OnXuxpe9S-N4KWA7lX5-pndLoHP3wvxWTXtxHgBxZaBXN4y18Ang', 1, '2026-02-25 18:57:43', '2026-03-05 00:27:46', 0);
INSERT INTO `user` VALUES (3, '123', 'e10adc3949ba59abbe56e057f20f883e', 0, 'eyJhbGciOiJIUzUxMiJ9.eyJyb2xlIjowLCJleHAiOjE3NzI3MTg4MzksInVzZXJJZCI6MywiaWF0IjoxNzcyNjMyNDM5LCJ1c2VybmFtZSI6IjEyMyJ9.lwsh5W6atpWlU8P1En1naWtWwjOVmUe22ZcP0o-Y1X-bHy-27PnjiTrNhzabdn-e0PuD62bQe1LrYWS8eyEOJg', 1, '2026-03-04 21:54:00', '2026-03-04 21:53:59', 0);

-- ----------------------------
-- Table structure for user_preference
-- ----------------------------
DROP TABLE IF EXISTS `user_preference`;
CREATE TABLE `user_preference`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '偏好ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `food_id` bigint NOT NULL COMMENT '菜品ID',
  `preference_score` decimal(5, 2) NOT NULL DEFAULT 0.00 COMMENT '偏好得分',
  `order_count` int NOT NULL DEFAULT 0 COMMENT '点餐次数',
  `is_collected` tinyint NOT NULL DEFAULT 0 COMMENT '是否收藏:0-否,1-是',
  `comment_rating` tinyint NULL DEFAULT NULL COMMENT '评论评分',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_user_food`(`user_id` ASC, `food_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_food_id`(`food_id` ASC) USING BTREE,
  INDEX `idx_preference_score`(`preference_score` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户偏好表(协同过滤算法)' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_preference
-- ----------------------------
INSERT INTO `user_preference` VALUES (1, 3, 8, 0.00, 0, 0, NULL, '2026-03-04 21:55:44', '2026-03-04 21:55:44');

SET FOREIGN_KEY_CHECKS = 1;

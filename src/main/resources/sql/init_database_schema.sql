SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS `ai_guide_platform`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE `ai_guide_platform`;

-- 1. 用户表（角色直接存在 role_code 字段）
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(32) NOT NULL COMMENT '登录账号',
  `password` VARCHAR(64) NOT NULL COMMENT '加密密码',
  `nickname` VARCHAR(32) NOT NULL COMMENT '用户昵称',
  `avatar_url` VARCHAR(255) DEFAULT NULL COMMENT '头像地址',
  `role_code` VARCHAR(16) NOT NULL DEFAULT 'TOURIST' COMMENT '角色编码 TOURIST/ADMIN',
  `user_status` TINYINT NOT NULL DEFAULT 1 COMMENT '用户状态 1启用 0禁用',
  `email` VARCHAR(64) DEFAULT NULL COMMENT '邮箱地址',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_role_code` (`role_code`),
  KEY `idx_user_status` (`user_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 2. 景点分类表
CREATE TABLE IF NOT EXISTS `scenic_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `category_name` VARCHAR(64) NOT NULL COMMENT '分类名称',
  `category_desc` VARCHAR(255) DEFAULT NULL COMMENT '分类描述',
  `sort_no` INT NOT NULL DEFAULT 0 COMMENT '排序号',
  `category_status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 0禁用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
  PRIMARY KEY (`id`),
  KEY `idx_category_status` (`category_status`),
  KEY `idx_sort_no` (`sort_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='景点分类表';

-- 3. 景点主表
CREATE TABLE IF NOT EXISTS `scenic_spot` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `category_id` BIGINT NOT NULL COMMENT '分类ID',
  `spot_name` VARCHAR(128) NOT NULL COMMENT '景点名称',
  `city` VARCHAR(64) DEFAULT NULL COMMENT '所在城市',
  `address` VARCHAR(255) DEFAULT NULL COMMENT '详细地址',
  `longitude` DECIMAL(10,6) DEFAULT NULL COMMENT '经度',
  `latitude` DECIMAL(10,6) DEFAULT NULL COMMENT '纬度',
  `cover_url` VARCHAR(255) DEFAULT NULL COMMENT '封面图地址',
  `summary` VARCHAR(500) DEFAULT NULL COMMENT '景点简介',
  `description` LONGTEXT COMMENT '景点详细介绍',
  `open_time` VARCHAR(128) DEFAULT NULL COMMENT '开放时间',
  `suggest_duration` VARCHAR(32) DEFAULT NULL COMMENT '建议游览时长',
  `tips` VARCHAR(500) DEFAULT NULL COMMENT '服务提示',
  `hot_score` INT NOT NULL DEFAULT 0 COMMENT '热度值',
  `spot_status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1上架 0下架',
  `creator_id` BIGINT NOT NULL COMMENT '创建人ID',
  `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览量',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_city` (`city`),
  KEY `idx_spot_status` (`spot_status`),
  KEY `idx_hot_score` (`hot_score`),
  KEY `idx_spot_name` (`spot_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='景点主表';

-- 4. 景点多语种内容表
CREATE TABLE IF NOT EXISTS `scenic_spot_i18n` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `scenic_spot_id` BIGINT NOT NULL COMMENT '景点ID',
  `language_code` VARCHAR(16) NOT NULL COMMENT '语言编码 zh-CN/en-US/ja-JP/ko-KR',
  `title` VARCHAR(128) DEFAULT NULL COMMENT '多语种标题',
  `summary` VARCHAR(500) DEFAULT NULL COMMENT '多语种摘要',
  `description` LONGTEXT COMMENT '多语种详细介绍',
  `tips` VARCHAR(500) DEFAULT NULL COMMENT '多语种注意事项',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_spot_lang` (`scenic_spot_id`, `language_code`),
  KEY `idx_language_code` (`language_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='景点多语种内容表';

-- 5. 景点媒体资源表
CREATE TABLE IF NOT EXISTS `scenic_spot_media` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `scenic_spot_id` BIGINT NOT NULL COMMENT '景点ID',
  `media_type` VARCHAR(16) NOT NULL COMMENT '媒体类型 image/audio/video',
  `media_url` VARCHAR(255) NOT NULL COMMENT '资源地址',
  `media_name` VARCHAR(128) DEFAULT NULL COMMENT '资源名称',
  `sort_no` INT NOT NULL DEFAULT 0 COMMENT '排序号',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
  PRIMARY KEY (`id`),
  KEY `idx_scenic_spot_id` (`scenic_spot_id`),
  KEY `idx_media_type` (`media_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='景点媒体资源表';

-- 6. 路线主表
CREATE TABLE IF NOT EXISTS `guide_route` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `route_name` VARCHAR(128) NOT NULL COMMENT '路线名称',
  `theme` VARCHAR(64) DEFAULT NULL COMMENT '主题标签 如:历史文化/自然风光/亲子游',
  `cover_url` VARCHAR(255) DEFAULT NULL COMMENT '封面图地址',
  `summary` VARCHAR(500) DEFAULT NULL COMMENT '路线简介',
  `description` LONGTEXT COMMENT '路线详细介绍',
  `suggest_duration` VARCHAR(32) DEFAULT NULL COMMENT '预计游玩时长',
  `suitable_crowd` VARCHAR(128) DEFAULT NULL COMMENT '适合人群 如:亲子/情侣/独行',
  `route_status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1上架 0下架',
  `creator_id` BIGINT NOT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
  PRIMARY KEY (`id`),
  KEY `idx_route_status` (`route_status`),
  KEY `idx_theme` (`theme`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='路线主表';

-- 7. 路线景点关联表
CREATE TABLE IF NOT EXISTS `guide_route_spot_rel` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `route_id` BIGINT NOT NULL COMMENT '路线ID',
  `scenic_spot_id` BIGINT NOT NULL COMMENT '景点ID',
  `sort_no` INT NOT NULL DEFAULT 0 COMMENT '景点在路线中的顺序',
  `stay_duration` VARCHAR(32) DEFAULT NULL COMMENT '建议停留时长',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
  PRIMARY KEY (`id`),
  KEY `idx_route_id` (`route_id`),
  KEY `idx_scenic_spot_id` (`scenic_spot_id`),
  KEY `uk_route_spot` (`route_id`, `scenic_spot_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='路线景点关联表';

-- 8. 路线多语种内容表
CREATE TABLE IF NOT EXISTS `guide_route_i18n` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `route_id` BIGINT NOT NULL COMMENT '路线ID',
  `language_code` VARCHAR(16) NOT NULL COMMENT '语言编码',
  `title` VARCHAR(128) DEFAULT NULL COMMENT '多语种标题',
  `summary` VARCHAR(500) DEFAULT NULL COMMENT '多语种简介',
  `description` LONGTEXT COMMENT '多语种详细介绍',
  `travel_tips` VARCHAR(500) DEFAULT NULL COMMENT '多语种出行提示',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_route_lang` (`route_id`, `language_code`),
  KEY `idx_language_code` (`language_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='路线多语种内容表';

-- 9. 收藏表
CREATE TABLE IF NOT EXISTS `user_favorite` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `biz_type` VARCHAR(16) NOT NULL COMMENT '收藏类型 SCENIC/ROUTE',
  `biz_id` BIGINT NOT NULL COMMENT '收藏对象ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_biz` (`user_id`, `biz_type`, `biz_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- 10. 用户反馈表
CREATE TABLE IF NOT EXISTS `user_feedback` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `feedback_type` VARCHAR(32) NOT NULL COMMENT '反馈类型 BUG/SUGGESTION/COMPLAINT/OTHER',
  `content` VARCHAR(2000) NOT NULL COMMENT '反馈内容',
  `contact_info` VARCHAR(128) DEFAULT NULL COMMENT '联系方式',
  `feedback_status` TINYINT NOT NULL DEFAULT 0 COMMENT '处理状态 0待处理 1已处理 2已忽略',
  `reply_content` VARCHAR(1000) DEFAULT NULL COMMENT '回复内容',
  `reply_time` DATETIME DEFAULT NULL COMMENT '回复时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_feedback_status` (`feedback_status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户反馈表';

-- 11. 文件资源表
CREATE TABLE IF NOT EXISTS `file_resource` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `biz_type` VARCHAR(64) DEFAULT NULL COMMENT '业务类型',
  `biz_id` BIGINT DEFAULT NULL COMMENT '业务ID',
  `file_name` VARCHAR(128) NOT NULL COMMENT '原始文件名',
  `file_url` VARCHAR(255) NOT NULL COMMENT '文件访问地址',
  `file_type` VARCHAR(32) DEFAULT NULL COMMENT '文件类型后缀',
  `file_size` BIGINT NOT NULL DEFAULT 0 COMMENT '文件大小 字节',
  `storage_mode` VARCHAR(32) NOT NULL DEFAULT 'oss' COMMENT '存储模式 oss/local',
  `uploader_id` BIGINT NOT NULL COMMENT '上传人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
  PRIMARY KEY (`id`),
  KEY `idx_biz_type_biz_id` (`biz_type`, `biz_id`),
  KEY `idx_uploader_id` (`uploader_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件资源表';

-- 12. AI问答日志表
CREATE TABLE IF NOT EXISTS `ai_guide_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT DEFAULT NULL COMMENT '调用用户ID',
  `model_name` VARCHAR(64) NOT NULL COMMENT '模型名称',
  `biz_type` VARCHAR(32) NOT NULL COMMENT '业务类型 EXPLAIN/COMPARE/RECOMMEND/TRANSLATE/QUESTION',
  `language_code` VARCHAR(16) DEFAULT NULL COMMENT '目标语言',
  `scenic_spot_id` BIGINT DEFAULT NULL COMMENT '关联景点ID',
  `route_id` BIGINT DEFAULT NULL COMMENT '关联路线ID',
  `request_summary` VARCHAR(4000) DEFAULT NULL COMMENT '请求摘要',
  `response_summary` VARCHAR(4000) DEFAULT NULL COMMENT '响应摘要',
  `success_flag` TINYINT NOT NULL DEFAULT 1 COMMENT '调用结果 1成功 0失败',
  `cost_millis` BIGINT NOT NULL DEFAULT 0 COMMENT '调用耗时毫秒',
  `error_message` VARCHAR(1000) DEFAULT NULL COMMENT '错误信息',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_biz_type` (`biz_type`),
  KEY `idx_success_flag` (`success_flag`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI问答日志表';

-- 13. AI聊天记录表
CREATE TABLE IF NOT EXISTS `ai_chat_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT DEFAULT NULL COMMENT '用户ID',
  `scenic_spot_id` BIGINT DEFAULT NULL COMMENT '关联景点ID',
  `route_id` BIGINT DEFAULT NULL COMMENT '关联路线ID',
  `question_type` VARCHAR(32) NOT NULL COMMENT '问题类型 QUESTION/EXPLAIN/COMPARE/RECOMMEND/TRANSLATE',
  `question_content` VARCHAR(4000) DEFAULT NULL COMMENT '问题内容',
  `prompt_text` VARCHAR(4000) DEFAULT NULL COMMENT '提示词摘要',
  `answer_content` LONGTEXT COMMENT 'AI回答内容',
  `model_name` VARCHAR(64) NOT NULL COMMENT '模型名称',
  `language_code` VARCHAR(16) DEFAULT NULL COMMENT '语言编码',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_question_type` (`question_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI聊天记录表';

SET FOREIGN_KEY_CHECKS = 1;

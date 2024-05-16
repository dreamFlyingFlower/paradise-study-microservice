CREATE TABLE `async_request` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `app_name` varchar(100) NOT NULL DEFAULT '' COMMENT '应用名称',
  `sign` varchar(50) NOT NULL DEFAULT '' COMMENT '方法签名',
  `class_name` varchar(200) NOT NULL DEFAULT '' COMMENT '全路径类名称',
  `method_name` varchar(100) NOT NULL DEFAULT '' COMMENT '方法名称',
  `async_type` varchar(50) NOT NULL DEFAULT '' COMMENT '异步策略类型',
  `exec_status` tinyint NOT NULL DEFAULT '0' COMMENT '执行状态:0-初始化;1-执行失败;2:执行成功',
  `exec_count` int NOT NULL DEFAULT '0' COMMENT '执行次数',
  `param_json` longtext COMMENT '请求参数',
  `remark` varchar(200) NOT NULL DEFAULT '' COMMENT '业务描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_app_name` (`app_name`) USING BTREE,
  KEY `idx_exec_status` (`exec_status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='异步处理请求';

CREATE TABLE `async_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `async_id` bigint NOT NULL DEFAULT '0' COMMENT '异步请求ID',
  `error_data` longtext COMMENT '执行错误信息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_async_id` (`async_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='异步处理日志';
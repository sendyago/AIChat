DROP TABLE IF EXISTS chat_info;
CREATE TABLE chat_info(
    `chat_id` VARCHAR(50) NOT NULL  COMMENT '会话ID' ,
    `user_content` TEXT NOT NULL  COMMENT '用户询问信息' ,
    `resp_content` TEXT NOT NULL  COMMENT 'AI回复信息' ,
    `chat_time` BIGINT NOT NULL  COMMENT '聊天时间' 
)  COMMENT = '聊天记录';

DROP TABLE IF EXISTS chat_title;
CREATE TABLE chat_title(
    `chat_id` VARCHAR(50) NOT NULL  COMMENT '会话ID' ,
    `title_name` VARCHAR(30) NOT NULL  COMMENT '会话标题' ,
    `chat_start_time` BIGINT NOT NULL  COMMENT '会话开始时间' ,
    `user_id` VARCHAR(50) NOT NULL  COMMENT '用户ID' ,
    `model_name` VARCHAR(100)   COMMENT '模型名称' 
)  COMMENT = '会话标题';

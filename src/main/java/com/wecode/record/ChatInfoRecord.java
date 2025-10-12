package com.wecode.record;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 聊天会话
 * @author wecode
 */
@TableName("chat_info")
public record ChatInfoRecord(
        @TableField("chat_id") String chatId,  // ID
        @TableField("user_content") String userContent, // 用户提问
        @TableField("resp_content") String chatContent, // ai回复
        @TableField("chat_time") Long chatTime   // 单条聊天时间
) {
}

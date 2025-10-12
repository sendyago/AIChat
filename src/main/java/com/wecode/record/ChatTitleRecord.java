package com.wecode.record;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 聊天标题
 * @author wecode
 */
@TableName("chat_title")
public record ChatTitleRecord(
        @TableField("chat_id") String chatId,  // chatId
        @TableField("title_name") String titleName, // chat名称
        @TableField("chat_start_time") Long chatStartTime, // chat开始时间（年月日时分秒）
        @TableField("user_id") String userId, // 用户id
        @TableField("model_name") String modelName // 模型名称
) {
}

package com.wecode.record.vo;

/**
 * 接收前端传递的参数
 * @author wecode
 */
public record ChatVo(
        String chatId,   // 会话id
        String chatText,  // 用户聊天内容
        String repText,  // ai回复
        String apiKey,  // apikey
        String chatModel,  // 选择会话的模型
        Boolean isHistory, // 是否使用历史聊天继续聊天
        Boolean isNew,    // 是否为新的会话
        String userName   // 用户名，不为空时可保存聊天到数据库
) {

}

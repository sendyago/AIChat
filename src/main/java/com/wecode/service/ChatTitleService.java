package com.wecode.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.wecode.mapper.ChatTitleMapper;
import com.wecode.record.ChatTitleRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * chat标题信息处理
 * @author wecode
 */
@Service
public class ChatTitleService {

    @Autowired
    private ChatTitleMapper chatTitleMapper;

    /**
     * 查询所有chat标题信息
     */
    public List<ChatTitleRecord> getChatTitleList(String userId) {
        return chatTitleMapper.selectList(
                new QueryWrapper<ChatTitleRecord>()
                        .eq("user_id", userId)
                        .orderByDesc("chat_start_time"));
    }

    /**
     * 新增聊天标题
     */
    public int save(ChatTitleRecord chatTitleRecord) {
        return chatTitleMapper.insert(chatTitleRecord);
    }

    /**
     * 根据chatId删除会话信息
     */
    public int delete(String chatId) {
        return chatTitleMapper.delete(new QueryWrapper<ChatTitleRecord>()
                .eq("chat_id", chatId));
    }

    /**
     * 根据chatId更新标题名称
     */
    public int edit(String chatId, String titleName) {
        ChatTitleRecord chatTitleRecord = new ChatTitleRecord(null, titleName,
                null, null, null);
        return chatTitleMapper.update(chatTitleRecord,
                new UpdateWrapper<ChatTitleRecord>().eq("chat_id", chatId));
    }

}

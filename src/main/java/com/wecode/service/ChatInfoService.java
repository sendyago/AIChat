package com.wecode.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wecode.mapper.ChatInfoMapper;
import com.wecode.record.ChatInfoRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 聊天会话（上下文）信息处理
 * @author wecode
 */
@Service
public class ChatInfoService {

    @Autowired
    private ChatInfoMapper chatInfoMapper;

    /**
     * 根据chatId查询聊天上下文
     */
    public List<ChatInfoRecord> getChatInfoList(String chatId) {
        return chatInfoMapper.selectList(
                new QueryWrapper<ChatInfoRecord>()
                        .eq("chat_id", chatId)
                        .orderByAsc("chat_time"));
    }

    /**
     * 新增每次聊天的内容
     */
    public int save(ChatInfoRecord chatInfoRecord) {
        return chatInfoMapper.insert(chatInfoRecord);
    }

    /**
     * 根据chatId删除会话信息
     */
    public int delete(String chatId) {
        return chatInfoMapper.delete(new QueryWrapper<ChatInfoRecord>()
                .eq("chat_id", chatId));
    }
}

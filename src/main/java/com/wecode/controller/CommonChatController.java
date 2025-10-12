package com.wecode.controller;

import com.wecode.base.ResultInfo;
import com.wecode.record.vo.ChatVo;
import com.wecode.service.ChatInfoService;
import com.wecode.service.ChatTitleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * chat通用接口类
 * @author wecode
 */
@RestController
@RequestMapping("/common")
public class CommonChatController {

    @Autowired
    private ChatTitleService chatTitleService;
    @Autowired
    private ChatInfoService chatInfoService;

    /**
     * 根据用户ID查询对应的chat标题
     */
    @GetMapping("/titles/{userId}")
    public ResultInfo<Object> getChatTitleList(@PathVariable("userId") String userId) {
        var chatList = chatTitleService.getChatTitleList(userId);
        return new ResultInfo<>(chatList);
    }

    /**
     * 根据会话id查询会话的内容
     */
    @GetMapping("/chats/{chatId}")
    public ResultInfo<Object> getChatList(@PathVariable("chatId") String chatId) {
        var chatInfoList = chatInfoService.getChatInfoList(chatId);
        return new ResultInfo<>(chatInfoList);
    }

    /**
     * 根据会话id删会话内容
     */
    @DeleteMapping("/delete/{chatId}")
    public ResultInfo<Object> delChat(@PathVariable("chatId") String chatId) {
        // 删除chat title
        chatTitleService.delete(chatId);
        // 删除chat info
        chatInfoService.delete(chatId);
        return new ResultInfo<>();
    }

    /**
     * 修改会话的标题
     */
    @PutMapping("/edit")
    public ResultInfo<Object> editChatTitle(@RequestBody ChatVo chatVo) {
        chatTitleService.edit(chatVo.chatId(), chatVo.chatText());
        return new ResultInfo<>();
    }
}

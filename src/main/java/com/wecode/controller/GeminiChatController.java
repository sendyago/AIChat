package com.wecode.controller;

import com.wecode.base.MessageInfo;
import com.wecode.base.ResultInfo;
import com.wecode.handler.GeminiChatHandler;
import com.wecode.record.vo.ChatVo;
import com.wecode.service.ChatInfoService;
import com.wecode.service.ChatTitleService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * Google Gemini API调用接口
 * @author wecode
 */
@RestController
@RequestMapping("/gemini")
public class GeminiChatController {

    private static final Logger logger = LoggerFactory.getLogger(GeminiChatController.class);
    private static final String CHAT_ID = "CHAT_ID";

    @Autowired
    private GeminiChatHandler geminiChatHandler;
    @Autowired
    private ChatInfoService chatInfoService;

    /**
     * 与Google Gemini AI聊天
     */
    @PostMapping("/chat")
    public ResultInfo<Object> chat(HttpServletRequest request, @RequestBody ChatVo chatVo) {
        var responseStr = "";
        try {
            // 校验入参
            var errorRepStr = geminiChatHandler.validateInfo(chatVo);
            if (errorRepStr != null) {
                return new ResultInfo<>(MessageInfo.FAILURE, errorRepStr);
            }
            // 对话有上下文
            responseStr = geminiChatHandler.chatSessionSingle(request, chatVo);
            return new ResultInfo<>(responseStr);
        } catch (Exception e) {
            logger.error("普通会话调用API过程错误：", e);
            return new ResultInfo<>(MessageInfo.FAILURE, "服务暂时不可用。");
        }
    }

    /**
     * 新的聊天
     */
    @PostMapping("/newChat")
    public ResultInfo<Object> newChat(HttpServletRequest request, @RequestBody ChatVo chatVo) {
        var responseStr = "";
        // 校验入参
        var errorRepStr = geminiChatHandler.validateInfo(chatVo);
        if (errorRepStr != null) {
            return new ResultInfo<>(MessageInfo.FAILURE, errorRepStr);
        }
        try {
            if (StringUtils.hasText(chatVo.chatText())) {
                // 对话有上下文
                responseStr = geminiChatHandler.chatSessionSingle(request, chatVo);
            } else {
                responseStr = "请输入要询问的内容。";
            }
            var chatId = (String) request.getSession().getAttribute(CHAT_ID);
            var repChatVo = new ChatVo(chatId, chatVo.chatText(), responseStr, chatVo.apiKey(),
                    chatVo.chatModel(), chatVo.isHistory(), chatVo.isNew(), chatVo.userName());
            return new ResultInfo<>(repChatVo);
        } catch (Exception e) {
            logger.error("创建新会话时调用API过程错误：", e);
            responseStr = "服务暂时不可用。";
            var repChatVo = new ChatVo("", chatVo.chatText(), responseStr, chatVo.apiKey(),
                    chatVo.chatModel(), chatVo.isHistory(), chatVo.isNew(), chatVo.userName());
            return new ResultInfo<>(repChatVo);
        }
    }

    /**
     * 使用历史记录继续聊天
     */
    @PostMapping("/historyChat")
    public ResultInfo<Object> chatWithHistory(HttpServletRequest request, @RequestBody ChatVo chatVo) {
        // 校验入参
        var errorRepStr = geminiChatHandler.validateInfo(chatVo);
        if (errorRepStr != null) {
            return new ResultInfo<>(MessageInfo.FAILURE, errorRepStr);
        }
        if (StringUtils.hasText(chatVo.chatId())) {
            var chatInfoList = chatInfoService.getChatInfoList(chatVo.chatId());
            try {
                // 将本地聊天历史上传
                var responseStr = geminiChatHandler.chatSessionSingle(request, chatVo, chatInfoList);
                return new ResultInfo<>(responseStr);
            } catch (Exception e) {
                logger.error("进行历史会话时调用API过程错误：", e);
                return new ResultInfo<>(MessageInfo.FAILURE, "服务暂时不可用。");
            }
        } else {
            return new ResultInfo<>(MessageInfo.FAILURE, "提供的数据不完整，无法进行会话聊天。");
        }
    }


}

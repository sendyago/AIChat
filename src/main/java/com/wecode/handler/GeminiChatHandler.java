package com.wecode.handler;

import com.google.genai.Chat;
import com.google.genai.Client;
import com.google.genai.errors.GenAiIOException;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GoogleSearch;
import com.google.genai.types.Part;
import com.google.genai.types.ThinkingConfig;
import com.google.genai.types.Tool;
import com.wecode.record.ChatInfoRecord;
import com.wecode.record.ChatTitleRecord;
import com.wecode.record.vo.ChatVo;
import com.wecode.service.ChatInfoService;
import com.wecode.service.ChatTitleService;
import com.wecode.utils.DateUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static java.lang.ScopedValue.where;

/**
 * Gemini Chat 业务处理
 * @author wecode
 */
@Component
public class GeminiChatHandler {

    private static final Logger logger = LoggerFactory.getLogger(GeminiChatHandler.class);
    private static final String CHAT_SESSION_KEY = "CHAT_SESSION_KEY";
    private static final String CHAT_ID = "CHAT_ID";
    private static final String USER_CHAT_KEY = "USER_CHAT_KEY";
    private static final String GEMINI25PRO = "gemini-2.5-pro";

    @Autowired
    private ChatTitleService chatTitleService;
    @Autowired
    private ChatInfoService chatInfoService;

    /**
     * 每个人有自己的chat上下文（聊天记录会保存到库）
     * @param chatVo
     *          chatText 用户输入聊天内容
     *          chatId 如果是历史聊天，则使用这个ID
     *          isHistory 是否从某个历史聊天开始，默认为false
     *          isNew 是否是新的对话，是则重新创建对话及ID
     * @return ai回复
     */
    public String chatSessionSingle(HttpServletRequest request, ChatVo chatVo) {
        return chatSessionSingle(request, chatVo, null);
    }

    /**
     * 每个人有自己的chat上下文（聊天记录会保存到库）
     * @param chatVo
     *          chatText 用户输入聊天内容
     *          chatId 如果是历史聊天，则使用这个ID
     *          isHistory 是否从某个历史聊天开始，默认为false
     *          isNew 是否是新的对话，是则重新创建对话及ID
     * @param chatInfoList 历史聊天记录，不为空时将历史传给远程api
     * @return ai回复
     */
    public String chatSessionSingle(HttpServletRequest request, ChatVo chatVo, List<ChatInfoRecord> chatInfoList) {
        var object = request.getSession().getAttribute(CHAT_SESSION_KEY);
        var chatIdObj = request.getSession().getAttribute(CHAT_ID);
        var userChatsObj = request.getSession().getAttribute(USER_CHAT_KEY);
        Map<String, Chat> chatMap = null;
        var isFirst = false;
        var setHistory = true;
        Chat chatSession = null;
        String chatId;
        if (userChatsObj != null) {
            chatMap = (Map<String, Chat>) userChatsObj;
        }
        if (chatMap == null) {
            chatMap = new HashMap<>();
        }
        if (chatVo.isNew() || chatVo.isHistory()) {
            boolean isPut = true;
            // 本地所有聊天会话
            // 是否从某个聊天历史开始的聊天
            if (chatVo.isHistory()) {
                chatId = chatVo.chatId();
                if (chatMap.containsKey(chatId)) {
                    chatSession = chatMap.get(chatId);
                    setHistory = false;
                    isPut = false;
                }
            } else {
                isFirst = true;
                chatId = UUID.randomUUID().toString();
            }
            if (isPut) {
                chatSession = getNewChatSession(chatVo.chatModel(), chatVo.apiKey());
                chatMap.put(chatId, chatSession);
                request.getSession().setAttribute(CHAT_SESSION_KEY, chatSession);
                request.getSession().setAttribute(CHAT_ID, chatId);
                request.getSession().setAttribute(USER_CHAT_KEY, chatMap);
            }
        } else {
            // 选择了某个聊天记录继续
            chatSession = (Chat) object;
            chatId = (String) chatIdObj;
        }
        // AI回复内容
        String responseStr = "";
        if (!Objects.isNull(chatSession)) {
            try {
                // 设置模型不思考，默认思考
                int thinkingBudget = 0;
                if (GEMINI25PRO.equals(chatVo.chatModel())) {
                    // gemini-2.5-pro必须思考，不支持不思考
                    thinkingBudget = -1;
                }
                ThinkingConfig thinkingConfig = ThinkingConfig.builder().thinkingBudget(thinkingBudget).build();
                Tool searchTool = Tool.builder().googleSearch(GoogleSearch.builder()).build();
                GenerateContentConfig config = GenerateContentConfig.builder()
                        .thinkingConfig(thinkingConfig)
                        .tools(searchTool)
                        .build();
                if (chatVo.isHistory()) {
                    if (setHistory) {
                        // 如果使用之前的历史聊天继续，则需要先将聊天记录进行组装并上传给gemini
                        var contentList = genContent(chatInfoList, chatVo.chatText());
                        var response = chatSession.sendMessage(contentList, config);
                        responseStr = response.text();
                    } else {
                        var response = chatSession.sendMessage(chatVo.chatText(), config);
                        responseStr = response.text();
                    }
                } else {
                    var response = chatSession.sendMessage(chatVo.chatText(), config);
                    responseStr = response.text();
                }
            } catch (GenAiIOException e) {
                responseStr = "网络出现问题，请稍候再试！";
                logger.error("与Gemini对话请求错误，请求超时：", e);
            } catch (Exception e) {
                responseStr = "调用远程API错误，请稍候再试，或切换其他模型。";
                logger.error("与Gemini对话请求错误，其他错误：", e);
            }
        }
        // 用户不为空时才保存，否则不保存
        if (StringUtils.hasText(chatVo.userName())) {
            // 保存聊天信息
            if (isFirst) {
                // 创建聊天标题
                saveChatTitle(chatVo.userName(), chatVo.chatText(), chatId, chatVo.chatModel());
            }
            // 保存聊天内容
            saveChatInfo(chatId, chatVo.chatText(), responseStr);
        }
        return responseStr;
    }

    /**
     * 创建聊天会话，远程可临时保留上下文
     */
    private Chat getNewChatSession(String chatModel, String apiKey) {
        var client = Client.builder().apiKey(apiKey).build();
        return client.chats.create(chatModel);
    }

    /**
     * 组装历史聊天
     */
    private List<Content> genContent(List<ChatInfoRecord> chatInfoList, String thisAak) {
        List<Content> contentList = new ArrayList<>();
        if (CollectionUtils.isEmpty(chatInfoList)) {
            return contentList;
        }
        // 将聊天历史内容组装
        for (var chatInfo : chatInfoList) {

            var userPart = Part.fromText(chatInfo.userContent());
            var userContent = Content.builder().role("user").parts(List.of(userPart)).build();
            contentList.add(userContent);
            var modelPart = Part.fromText(chatInfo.chatContent());
            var modelContent = Content.builder().role("model").parts(List.of(modelPart)).build();
            contentList.add(modelContent);
        }
        var userPart = Part.fromText(thisAak);
        var userContent = Content.builder().role("user").parts(List.of(userPart)).build();
        contentList.add(userContent);
        return contentList;
    }

    /*
     * 保存新聊天标题
     */
    private void saveChatTitle(String userId, String userChat, String chatId, String modelName) {
        var titleName = userChat.length() > 30 ? userChat.substring(0, 30) : userChat;
        var chatStartTime = DateUtils.getLongDateTime();
        var chatTitle = new ChatTitleRecord(chatId, titleName, chatStartTime, userId, modelName);
        chatTitleService.save(chatTitle);
    }

    /*
     * 保存每次的聊天内容
     */
    private void saveChatInfo(String chatId, String userChat, String aiChat) {
        var chatTime = DateUtils.getLongDateTime();
        var chatInfo = new ChatInfoRecord(chatId, userChat, aiChat,chatTime);
        chatInfoService.save(chatInfo);
    }

    public String validateInfo(ChatVo chatVo) {
        if (chatVo == null) {
            return "提供的信息不完整";
        }
        if (!StringUtils.hasText(chatVo.apiKey())) {
            return "请输入ApiKey，获取地址：https://aistudio.google.com/api-keys";
        }
        if (!StringUtils.hasText(chatVo.chatModel())) {
            return "请选择一个聊天模型。";
        }
        if (!StringUtils.hasText(chatVo.chatText())) {
            return "请输入要询问的内容。";
        }
        return null;
    }
}

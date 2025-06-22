package com.tomato.tomato_mall.service.impl;

import com.tomato.tomato_mall.dto.ConversationCreateDTO;
import com.tomato.tomato_mall.dto.MessageCreateDTO;
import com.tomato.tomato_mall.entity.Conversation;
import com.tomato.tomato_mall.entity.Message;
import com.tomato.tomato_mall.entity.User;
import com.tomato.tomato_mall.entity.Message.Role;
import com.tomato.tomato_mall.enums.ErrorTypeEnum;
import com.tomato.tomato_mall.exception.BusinessException;
import com.tomato.tomato_mall.repository.ConversationRepository;
import com.tomato.tomato_mall.repository.MessageRepository;
import com.tomato.tomato_mall.repository.UserRepository;
import com.tomato.tomato_mall.service.ConversationService;
import com.tomato.tomato_mall.vo.ConversationVO;
import com.tomato.tomato_mall.vo.MessageVO;

import reactor.core.publisher.Flux;

import org.springframework.ai.chat.client.*;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI对话服务实现类
 * <p>
 * 实现AI对话和消息管理的核心业务逻辑
 * 包括对话创建、查询、删除和消息处理功能
 * 集成Spring AI客户端实现AI对话功能，支持聊天记忆和流式响应
 * </p>
 * 
 * @author Team CBDDL
 * @version 1.0
 */
@Service
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    /**
     * 构造函数，通过依赖注入初始化所需服务
     * 
     * @param conversationRepository 对话数据访问接口
     * @param messageRepository      消息数据访问接口
     * @param userRepository         用户数据访问接口
     * @param chatClient             Spring AI聊天客户端，用于与AI模型交互
     * @param chatMemory             聊天记忆组件，用于保存对话上下文
     */
    public ConversationServiceImpl(
            ConversationRepository conversationRepository,
            MessageRepository messageRepository,
            UserRepository userRepository,
            ChatClient chatClient,
            ChatMemory chatMemory) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.chatClient = chatClient;
        this.chatMemory = chatMemory;
    }

    @Override
    public List<ConversationVO> getUserConversations(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.USER_NOT_FOUND));

        List<Conversation> conversations = conversationRepository.findByUserOrderByUpdateTimeDesc(user);
        return conversations.stream()
                .map(this::convertToConversationVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ConversationVO createConversation(String username, ConversationCreateDTO createDTO) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.USER_NOT_FOUND));

        Conversation conversation = new Conversation();
        conversation.setUser(user);
        conversation.setTitle(createDTO.getTitle());

        Conversation savedConversation = conversationRepository.save(conversation);
        return convertToConversationVO(savedConversation, true);
    }

    @Override
    public ConversationVO getConversation(String username, String conversationId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.USER_NOT_FOUND));

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.CONVERSATION_NOT_FOUND));

        if (!conversation.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorTypeEnum.CONVERSATION_NOT_BELONG_TO_USER);
        }

        return convertToConversationVO(conversation, true);
    }

    @Override
    @Transactional
    public void deleteConversation(String username, String conversationId) {
        // 验证用户和会话
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.USER_NOT_FOUND));

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.CONVERSATION_NOT_FOUND));

        if (!conversation.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorTypeEnum.CONVERSATION_NOT_BELONG_TO_USER);
        }

        String conversationKey = conversationId.substring(0, 12);
        chatMemory.clear(conversationKey);
        conversationRepository.delete(conversation);
    }

    @Override
    @Transactional
    public MessageVO getMessage(String username, String conversationId, MessageCreateDTO messageDTO) {
        // 验证用户和会话
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.USER_NOT_FOUND));

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.CONVERSATION_NOT_FOUND));

        if (!conversation.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorTypeEnum.CONVERSATION_NOT_BELONG_TO_USER);
        }

        // 保存用户消息
        Message userMessage = new Message();
        userMessage.setConversation(conversation);
        userMessage.setRole(Role.USER);
        userMessage.setContent(messageDTO.getContent());
        userMessage = messageRepository.save(userMessage);

        String conversationKey = conversationId.substring(0, 12);

        try {
            String response = chatClient.prompt()
                    .user(messageDTO.getContent())
                    .advisors(a -> a.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, conversationKey))
                    .call()
                    .content();

            // 保存AI回复
            Message assistantMessage = new Message();
            assistantMessage.setConversation(conversation);
            assistantMessage.setRole(Role.ASSISTANT);
            assistantMessage.setContent(response);
            assistantMessage = messageRepository.save(assistantMessage);

            // 更新会话
            conversation.setUpdateTime(LocalDateTime.now());
            conversationRepository.save(conversation);

            return convertToMessageVO(assistantMessage);
        } catch (Exception e) {
            throw new BusinessException(ErrorTypeEnum.RESPONSE_FETCH_FAILED);
        }
    }

    @Override
    @Transactional
    public Flux<String> getStreamMessage(String username, String conversationId, MessageCreateDTO messageDTO) {
        // 验证用户和会话
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.USER_NOT_FOUND));
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.CONVERSATION_NOT_FOUND));
        if (!conversation.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorTypeEnum.CONVERSATION_NOT_BELONG_TO_USER);
        }

        // 保存用户消息
        Message userMessage = new Message();
        userMessage.setConversation(conversation);
        userMessage.setRole(Role.USER);
        userMessage.setContent(messageDTO.getContent());
        userMessage = messageRepository.save(userMessage);
        String conversationKey = conversationId.substring(0, 12);

        // 创建AI回复消息对象，但先不保存内容
        Message assistantMessage = new Message();
        assistantMessage.setConversation(conversation);
        assistantMessage.setRole(Role.ASSISTANT);
        assistantMessage.setContent(""); // 初始为空
        assistantMessage = messageRepository.save(assistantMessage);

        // 最终要保存的完整内容
        final StringBuilder completeResponse = new StringBuilder();
        final String assistantMessageId = assistantMessage.getId();

        // 获取流式回复并处理
        Flux<String> responseFlux = chatClient.prompt()
                .user(messageDTO.getContent())
                .advisors(a -> a.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, conversationKey))
                .stream()
                .content();

        // 转换流，同时收集完整回复
        return responseFlux
                .doOnNext(chunk -> {
                    // 累积响应内容
                    completeResponse.append(chunk);
                })
                .doOnComplete(() -> {
                    // 流完成时，保存完整响应到数据库
                    messageRepository.findById(assistantMessageId).ifPresent(msg -> {
                        msg.setContent(completeResponse.toString());
                        messageRepository.save(msg);

                        // 更新会话
                        conversation.setUpdateTime(LocalDateTime.now());
                        conversationRepository.save(conversation);
                    });
                });
    }

    /**
     * 将对话实体转换为视图对象（不包含消息）
     * <p>
     * 转换对话基本信息，用于对话列表显示
     * </p>
     * 
     * @param conversation 对话实体
     * @return 对话视图对象
     */
    private ConversationVO convertToConversationVO(Conversation conversation) {
        return convertToConversationVO(conversation, false);
    }

    /**
     * 将对话实体转换为视图对象
     * <p>
     * 转换对话信息，可选择是否包含完整的消息历史记录
     * 当hasMessages为true时，查询并转换所有相关消息
     * </p>
     * 
     * @param conversation 对话实体
     * @param hasMessages  是否包含消息历史
     * @return 对话视图对象
     */
    private ConversationVO convertToConversationVO(Conversation conversation, boolean hasMessages) {
        ConversationVO vo = new ConversationVO();
        BeanUtils.copyProperties(conversation, vo);

        if (hasMessages) {
            List<Message> messages = messageRepository.findByConversationOrderByCreateTime(conversation);
            List<MessageVO> messageVOs = messages.stream()
                    .map(this::convertToMessageVO)
                    .collect(Collectors.toList());
            vo.setMessages(messageVOs);
        }

        return vo;
    }

    /**
     * 将消息实体转换为视图对象
     * <p>
     * 转换消息信息，将角色枚举转换为字符串格式
     * </p>
     * 
     * @param message 消息实体
     * @return 消息视图对象
     */
    private MessageVO convertToMessageVO(Message message) {
        MessageVO vo = new MessageVO();
        BeanUtils.copyProperties(message, vo);
        vo.setRole(message.getRole().toString());
        return vo;
    }
}
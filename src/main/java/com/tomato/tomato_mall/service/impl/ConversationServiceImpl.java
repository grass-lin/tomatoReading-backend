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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

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
                .map(conversation -> {
                    ConversationVO conversationVO = convertToConversationVO(conversation);
                    conversationVO.setMessages(null);
                    return conversationVO;
                })
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
        return convertToConversationVO(savedConversation);
    }

    @Override
    public ConversationVO getConversation(String username, Long conversationId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.USER_NOT_FOUND));

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.CONVERSATION_NOT_FOUND));

        if (!conversation.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorTypeEnum.CONVERSATION_NOT_BELONG_TO_USER);
        }

        return convertToConversationVO(conversation);
    }

    @Override
    @Transactional
    public void deleteConversation(String username, Long conversationId) {
        // 验证用户和会话
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.USER_NOT_FOUND));

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.CONVERSATION_NOT_FOUND));

        if (!conversation.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorTypeEnum.CONVERSATION_NOT_BELONG_TO_USER);
        }

        String conversationKey = "conversation-" + conversationId;
        chatMemory.clear(conversationKey);
        conversationRepository.delete(conversation);
    }

    @Override
    @Transactional
    public MessageVO getMessage(String username, Long conversationId, MessageCreateDTO messageDTO) {
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

        String conversationKey = "conversation-" + conversationId;

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
            conversationRepository.save(conversation);

            return convertToMessageVO(assistantMessage);
        } catch (Exception e) {
            throw new BusinessException(ErrorTypeEnum.RESPONSE_FETCH_FAILED);
        }
    }

    @Override
    @Transactional
    public Flux<String> getStreamMessage(String username, Long conversationId, MessageCreateDTO messageDTO) {
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
        String conversationKey = "conversation-" + conversationId;

        // 创建AI回复消息对象，但先不保存内容
        Message assistantMessage = new Message();
        assistantMessage.setConversation(conversation);
        assistantMessage.setRole(Role.ASSISTANT);
        assistantMessage.setContent(""); // 初始为空
        assistantMessage = messageRepository.save(assistantMessage);

        // 最终要保存的完整内容
        final StringBuilder completeResponse = new StringBuilder();
        final Long assistantMessageId = assistantMessage.getId();

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
                        conversationRepository.save(conversation);
                    });
                });
    }

    private ConversationVO convertToConversationVO(Conversation conversation) {
        ConversationVO vo = new ConversationVO();
        BeanUtils.copyProperties(conversation, vo);

        List<Message> messages = messageRepository.findByConversationOrderByCreateTime(conversation);
        List<MessageVO> messageVOs = messages.stream()
                .map(this::convertToMessageVO)
                .collect(Collectors.toList());
        vo.setMessages(messageVOs);

        return vo;
    }

    private MessageVO convertToMessageVO(Message message) {
        MessageVO vo = new MessageVO();
        BeanUtils.copyProperties(message, vo);
        vo.setRole(message.getRole().toString());
        return vo;
    }
}
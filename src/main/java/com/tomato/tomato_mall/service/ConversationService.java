package com.tomato.tomato_mall.service;

import com.tomato.tomato_mall.dto.ConversationCreateDTO;
import com.tomato.tomato_mall.dto.MessageCreateDTO;
import com.tomato.tomato_mall.vo.ConversationVO;
import com.tomato.tomato_mall.vo.MessageVO;

import reactor.core.publisher.Flux;

import java.util.List;

/**
 * AI对话服务接口
 * <p>
 * 定义AI对话和消息管理的核心业务逻辑接口
 * 提供对话创建、查询、删除和消息处理功能
 * </p>
 * 
 * @author Team CBDDL
 * @version 1.0
 */
public interface ConversationService {

    /**
     * 获取用户所有对话
     * <p>
     * 根据用户名查询该用户的所有对话记录
     * </p>
     * 
     * @param username 用户名
     * @return 用户的对话列表
     */
    List<ConversationVO> getUserConversations(String username);

    /**
     * 创建新对话
     * <p>
     * 为指定用户创建一个新的AI对话会话
     * </p>
     * 
     * @param username  用户名
     * @param createDTO 对话创建数据传输对象，包含对话标题等初始化信息
     * @return 新创建的对话信息
     */
    ConversationVO createConversation(String username, ConversationCreateDTO createDTO);

    /**
     * 获取对话详情
     * <p>
     * 根据对话ID获取对话的详细信息，包含完整的消息历史记录
     * 验证用户权限，确保用户只能访问自己的对话
     * </p>
     * 
     * @param username       用户名
     * @param conversationId 对话ID
     * @return 对话详细信息
     */
    ConversationVO getConversation(String username, String conversationId);

    /**
     * 删除对话
     * <p>
     * 删除指定ID的对话记录及其所有相关消息
     * 验证用户权限，确保用户只能删除自己的对话
     * </p>
     * 
     * @param username       用户名
     * @param conversationId 要删除的对话ID
     */
    void deleteConversation(String username, String conversationId);

    /**
     * 发送消息并获取AI回复
     * <p>
     * 在指定对话中发送消息并获取AI的完整回复
     * 将用户消息和AI回复都保存到对话历史中
     * </p>
     * 
     * @param username       用户名
     * @param conversationId 对话ID
     * @param messageDTO     消息创建数据传输对象，包含用户发送的消息内容
     * @return AI回复消息
     */
    MessageVO getMessage(String username, String conversationId, MessageCreateDTO messageDTO);

    /**
     * 发送消息并获取AI流式回复
     * <p>
     * 在指定对话中发送消息并通过响应式流获取AI的实时回复
     * 返回的数据流包含消息片段，支持实时显示
     * </p>
     * 
     * @param username       用户名
     * @param conversationId 对话ID
     * @param messageDTO     消息创建数据传输对象，包含用户发送的消息内容
     * @return 响应式流，包含AI回复的消息片段
     */
    Flux<String> getStreamMessage(String username, String conversationId, MessageCreateDTO messageDTO);
}
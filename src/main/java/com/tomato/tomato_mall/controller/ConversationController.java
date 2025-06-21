package com.tomato.tomato_mall.controller;

import com.tomato.tomato_mall.dto.ConversationCreateDTO;
import com.tomato.tomato_mall.dto.MessageCreateDTO;
import com.tomato.tomato_mall.service.ConversationService;
import com.tomato.tomato_mall.vo.ConversationVO;
import com.tomato.tomato_mall.vo.MessageVO;
import com.tomato.tomato_mall.vo.ResponseVO;

import reactor.core.publisher.Flux;

import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI对话控制器
 * <p>
 * 提供AI对话和消息管理的REST API接口
 * 除流式信息外的接口返回统一的ResponseVO格式，包含状态码、消息和数据
 * </p>
 * 
 * @author Team CBDDL
 * @version 1.0
 */
@RestController
@RequestMapping("/api/ai")
public class ConversationController {

    private final ConversationService conversationService;

    /**
     * 构造函数，通过依赖注入初始化服务
     * 
     * @param conversationService 对话服务，处理AI对话相关业务逻辑
     */
    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    /**
     * 获取用户所有对话接口
     * <p>
     * 返回当前登录用户的所有对话列表
     * </p>
     * 
     * @return 返回包含用户对话列表的响应体，状态码200
     */
    @GetMapping
    public ResponseEntity<ResponseVO<List<ConversationVO>>> getUserConversations() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ConversationVO> conversations = conversationService.getUserConversations(username);
        return ResponseEntity.ok(ResponseVO.success(conversations));
    }

    /**
     * 创建新对话接口
     * <p>
     * 为当前登录用户创建一个新的AI对话会话
     * </p>
     * 
     * @param createDTO 对话创建数据传输对象，包含对话标题等初始化信息
     * @return 返回包含新创建对话信息的响应体，状态码200
     */
    @PostMapping
    public ResponseEntity<ResponseVO<ConversationVO>> createConversation(
            @RequestBody ConversationCreateDTO createDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ConversationVO conversation = conversationService.createConversation(username, createDTO);
        return ResponseEntity.ok(ResponseVO.success(conversation));
    }

    /**
     * 根据ID获取对话详情接口
     * <p>
     * 返回指定ID的对话详细信息，包含完整的消息历史记录
     * 用户只能访问自己的对话
     * </p>
     * 
     * @param conversationId 对话ID
     * @return 返回包含对话详情的响应体，状态码200
     */
    @GetMapping("/{conversationId}")
    public ResponseEntity<ResponseVO<ConversationVO>> getConversation(@PathVariable String conversationId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ConversationVO conversation = conversationService.getConversation(username, conversationId);
        return ResponseEntity.ok(ResponseVO.success(conversation));
    }

    /**
     * 删除对话接口
     * <p>
     * 删除指定ID的对话记录及其所有相关消息
     * 用户只能删除自己的对话
     * </p>
     * 
     * @param conversationId 要删除的对话ID
     * @return 返回成功消息的响应体，状态码200
     */
    @DeleteMapping("/{conversationId}")
    public ResponseEntity<ResponseVO<String>> deleteConversation(@PathVariable String conversationId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        conversationService.deleteConversation(username, conversationId);
        return ResponseEntity.ok(ResponseVO.success("删除成功"));
    }

    /**
     * 发送消息并获取AI回复接口
     * <p>
     * 在指定对话中发送消息并获取AI的完整回复
     * </p>
     * 
     * @param conversationId 对话ID
     * @param messageDTO     消息创建数据传输对象，包含用户发送的消息内容
     * @return 返回包含AI回复消息的响应体，状态码200
     */
    @PostMapping("/{conversationId}")
    public ResponseEntity<ResponseVO<MessageVO>> getMessage(
            @PathVariable String conversationId,
            @RequestBody MessageCreateDTO messageDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        MessageVO response = conversationService.getMessage(username, conversationId, messageDTO);
        return ResponseEntity.ok(ResponseVO.success(response));
    }

    /**
     * 发送消息并获取AI流式回复接口
     * <p>
     * 在指定对话中发送消息并通过SSE获取AI的实时流式回复
     * 返回的数据流包含消息片段，前端可以实时接收并拼接显示
     * </p>
     * 
     * @param conversationId 对话ID
     * @param messageDTO     消息创建数据传输对象，包含用户发送的消息内容
     * @return 返回SSE流，包含回复的消息片段
     */
    @PostMapping(value = "/stream/{conversationId}", produces = "text/event-stream;charset=UTF-8")
    public Flux<ServerSentEvent<String>> getStreamMessage(@PathVariable String conversationId,
            @RequestBody MessageCreateDTO messageDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return conversationService.getStreamMessage(username, conversationId, messageDTO)
                .map(message -> ServerSentEvent.<String>builder()
                        .id(String.valueOf(System.currentTimeMillis()))
                        .event("message")
                        .data(message)
                        .build())
                .onErrorResume(e -> {
                    return Flux.just(ServerSentEvent.<String>builder()
                            .id("error")
                            .event("error")
                            .data(e.getMessage())
                            .build());
                });
    }
}
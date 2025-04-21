package com.tomato.tomato_mall.controller;

import com.tomato.tomato_mall.dto.ConversationCreateDTO;
import com.tomato.tomato_mall.dto.MessageCreateDTO;
import com.tomato.tomato_mall.service.ConversationService;
import com.tomato.tomato_mall.vo.ConversationVO;
import com.tomato.tomato_mall.vo.MessageVO;
import com.tomato.tomato_mall.vo.ResponseVO;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping
    public ResponseEntity<ResponseVO<List<ConversationVO>>> getUserConversations() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ConversationVO> conversations = conversationService.getUserConversations(username);
        return ResponseEntity.ok(ResponseVO.success(conversations));
    }

    @PostMapping
    public ResponseEntity<ResponseVO<ConversationVO>> createConversation(
            @RequestBody ConversationCreateDTO createDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ConversationVO conversation = conversationService.createConversation(username, createDTO);
        return ResponseEntity.ok(ResponseVO.success(conversation));
    }

    @GetMapping("/{conversationId}")
    public ResponseEntity<ResponseVO<ConversationVO>> getConversation(@PathVariable Long conversationId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ConversationVO conversation = conversationService.getConversation(username, conversationId);
        return ResponseEntity.ok(ResponseVO.success(conversation));
    }

    @DeleteMapping("/{conversationId}")
    public ResponseEntity<ResponseVO<String>> deleteConversation(@PathVariable Long conversationId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        conversationService.deleteConversation(username, conversationId);
        return ResponseEntity.ok(ResponseVO.success("删除成功"));
    }

    @PostMapping("/{conversationId}")
    public ResponseEntity<ResponseVO<MessageVO>> sendMessage(
            @PathVariable Long conversationId,
            @RequestBody MessageCreateDTO messageDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        MessageVO response = conversationService.sendMessage(username, conversationId, messageDTO);
        return ResponseEntity.ok(ResponseVO.success(response));
    }
}
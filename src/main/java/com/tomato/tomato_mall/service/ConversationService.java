package com.tomato.tomato_mall.service;

import com.tomato.tomato_mall.dto.ConversationCreateDTO;
import com.tomato.tomato_mall.dto.MessageCreateDTO;
import com.tomato.tomato_mall.vo.ConversationVO;
import com.tomato.tomato_mall.vo.MessageVO;

import java.util.List;

public interface ConversationService {
    
    List<ConversationVO> getUserConversations(String username);
    
    ConversationVO createConversation(String username, ConversationCreateDTO createDTO);
    
    ConversationVO getConversation(String username, Long conversationId);
    
    void deleteConversation(String username, Long conversationId);
    
    MessageVO sendMessage(String username, Long conversationId, MessageCreateDTO messageDTO);
}
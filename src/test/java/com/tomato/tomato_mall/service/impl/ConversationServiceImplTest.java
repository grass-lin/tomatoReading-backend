package com.tomato.tomato_mall.service.impl;

import com.tomato.tomato_mall.dto.ConversationCreateDTO;
import com.tomato.tomato_mall.dto.MessageCreateDTO;
import com.tomato.tomato_mall.entity.Conversation;
import com.tomato.tomato_mall.entity.User;
import com.tomato.tomato_mall.enums.ErrorTypeEnum;
import com.tomato.tomato_mall.exception.BusinessException;
import com.tomato.tomato_mall.repository.ConversationRepository;
import com.tomato.tomato_mall.repository.MessageRepository;
import com.tomato.tomato_mall.repository.UserRepository;
import com.tomato.tomato_mall.vo.ConversationVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConversationServiceImplTest {

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatMemory chatMemory;

    @InjectMocks
    private ConversationServiceImpl conversationService;

    private User user;
    private Conversation conversation;
    private ConversationCreateDTO createDTO;
    private MessageCreateDTO messageDTO;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setName("Test User");

        conversation = new Conversation();
        conversation.setId("CONV-123456789012");
        conversation.setUser(user);
        conversation.setTitle("Test Conversation");
        conversation.setCreateTime(LocalDateTime.now());
        conversation.setUpdateTime(LocalDateTime.now());

        createDTO = new ConversationCreateDTO();
        createDTO.setTitle("New Conversation");

        messageDTO = new MessageCreateDTO();
        messageDTO.setContent("Hello AI");
    }

    // --- getUserConversations 方法测试 ---
    @Test
    void getUserConversations_Success() {
        // --- Arrange ---
        List<Conversation> conversations = Arrays.asList(conversation);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(conversationRepository.findByUserOrderByUpdateTimeDesc(user)).thenReturn(conversations);

        // --- Act ---
        List<ConversationVO> result = conversationService.getUserConversations("testuser");

        // --- Assert ---
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(conversation.getId(), result.get(0).getId());
        assertEquals(conversation.getTitle(), result.get(0).getTitle());

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(conversationRepository, times(1)).findByUserOrderByUpdateTimeDesc(user);
    }

    @Test
    void getUserConversations_UserNotFound() {
        // --- Arrange ---
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            conversationService.getUserConversations("testuser");
        });

        assertEquals(ErrorTypeEnum.USER_NOT_FOUND, exception.getErrorType());
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(conversationRepository, never()).findByUserOrderByUpdateTimeDesc(any());
    }

    @Test
    void getUserConversations_EmptyList() {
        // --- Arrange ---
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(conversationRepository.findByUserOrderByUpdateTimeDesc(user)).thenReturn(Arrays.asList());

        // --- Act ---
        List<ConversationVO> result = conversationService.getUserConversations("testuser");

        // --- Assert ---
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(conversationRepository, times(1)).findByUserOrderByUpdateTimeDesc(user);
    }

    // --- createConversation 方法测试 ---
    @Test
    void createConversation_Success() {
        // --- Arrange ---
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(conversationRepository.save(any(Conversation.class))).thenAnswer(invocation -> {
            Conversation savedConv = invocation.getArgument(0);
            savedConv.setId("CONV-123456789012");
            savedConv.setCreateTime(LocalDateTime.now());
            savedConv.setUpdateTime(LocalDateTime.now());
            return savedConv;
        });

        // --- Act ---
        ConversationVO result = conversationService.createConversation("testuser", createDTO);

        // --- Assert ---
        assertNotNull(result);
        assertEquals(createDTO.getTitle(), result.getTitle());
        assertNotNull(result.getId());

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(conversationRepository, times(1)).save(any(Conversation.class));
    }

    @Test
    void createConversation_UserNotFound() {
        // --- Arrange ---
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            conversationService.createConversation("testuser", createDTO);
        });

        assertEquals(ErrorTypeEnum.USER_NOT_FOUND, exception.getErrorType());
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(conversationRepository, never()).save(any());
    }

    // --- getConversation 方法测试 ---
    @Test
    void getConversation_Success() {
        // --- Arrange ---
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(conversationRepository.findById("CONV-123456789012")).thenReturn(Optional.of(conversation));

        // --- Act ---
        ConversationVO result = conversationService.getConversation("testuser", "CONV-123456789012");

        // --- Assert ---
        assertNotNull(result);
        assertEquals(conversation.getId(), result.getId());
        assertEquals(conversation.getTitle(), result.getTitle());

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(conversationRepository, times(1)).findById("CONV-123456789012");
    }

    @Test
    void getConversation_ConversationNotFound() {
        // --- Arrange ---
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(conversationRepository.findById("CONV-123456789012")).thenReturn(Optional.empty());

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            conversationService.getConversation("testuser", "CONV-123456789012");
        });

        assertEquals(ErrorTypeEnum.CONVERSATION_NOT_FOUND, exception.getErrorType());
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(conversationRepository, times(1)).findById("CONV-123456789012");
    }

    @Test
    void getConversation_ConversationNotBelongToUser() {
        // --- Arrange ---
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setUsername("otheruser");
        conversation.setUser(otherUser); // 设置为其他用户的会话

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(conversationRepository.findById("CONV-123456789012")).thenReturn(Optional.of(conversation));

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            conversationService.getConversation("testuser", "CONV-123456789012");
        });

        assertEquals(ErrorTypeEnum.CONVERSATION_NOT_BELONG_TO_USER, exception.getErrorType());
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(conversationRepository, times(1)).findById("CONV-123456789012");
    }

    // --- deleteConversation 方法测试 ---
    @Test
    void deleteConversation_Success() {
        // --- Arrange ---
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(conversationRepository.findById("CONV-123456789012")).thenReturn(Optional.of(conversation));

        // --- Act ---
        conversationService.deleteConversation("testuser", "CONV-123456789012");

        // --- Assert ---
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(conversationRepository, times(1)).findById("CONV-123456789012");
        verify(chatMemory, times(1)).clear("CONV-1234567"); // 取前12位
        verify(conversationRepository, times(1)).delete(conversation);
    }

    @Test
    void deleteConversation_UserNotFound() {
        // --- Arrange ---
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            conversationService.deleteConversation("testuser", "CONV-123456789012");
        });

        assertEquals(ErrorTypeEnum.USER_NOT_FOUND, exception.getErrorType());
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(conversationRepository, never()).findById(any());
        verify(chatMemory, never()).clear(any());
        verify(conversationRepository, never()).delete(any());
    }

    @Test
    void deleteConversation_ConversationNotFound() {
        // --- Arrange ---
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(conversationRepository.findById("CONV-123456789012")).thenReturn(Optional.empty());

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            conversationService.deleteConversation("testuser", "CONV-123456789012");
        });

        assertEquals(ErrorTypeEnum.CONVERSATION_NOT_FOUND, exception.getErrorType());
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(conversationRepository, times(1)).findById("CONV-123456789012");
        verify(chatMemory, never()).clear(any());
        verify(conversationRepository, never()).delete(any());
    }

    // 注意：getMessage 和 getStreamMessage 方法由于涉及复杂的 AI 客户端 Mock，
    // 在单元测试中较难准确模拟，建议在集成测试中进行测试
}

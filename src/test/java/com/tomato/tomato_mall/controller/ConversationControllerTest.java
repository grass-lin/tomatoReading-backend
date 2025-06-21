package com.tomato.tomato_mall.controller;

import com.tomato.tomato_mall.dto.ConversationCreateDTO;
import com.tomato.tomato_mall.dto.MessageCreateDTO;
import com.tomato.tomato_mall.service.ConversationService;
import com.tomato.tomato_mall.vo.ConversationVO;
import com.tomato.tomato_mall.vo.MessageVO;
import com.tomato.tomato_mall.vo.ResponseVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConversationControllerTest {

    @Mock
    private ConversationService conversationService;

    @InjectMocks
    private ConversationController conversationController;

    private ConversationCreateDTO createDTO;
    private MessageCreateDTO messageDTO;
    private ConversationVO conversationVO;
    private MessageVO messageVO;

    @BeforeEach
    void setUp() {
        createDTO = new ConversationCreateDTO();
        createDTO.setTitle("测试对话");

        messageDTO = new MessageCreateDTO();
        messageDTO.setContent("Hello AI");

        conversationVO = new ConversationVO();
        conversationVO.setId("CONV-123456789012");
        conversationVO.setTitle("测试对话");
        conversationVO.setCreateTime(LocalDateTime.now());
        conversationVO.setUpdateTime(LocalDateTime.now());

        messageVO = new MessageVO();
        messageVO.setId("MSG-123456789012");
        messageVO.setRole("user");
        messageVO.setContent("Hello AI");
        messageVO.setCreateTime(LocalDateTime.now());
    }

    private void mockSecurityContext(String username) {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetUserConversations_Success() throws Exception {
        // --- Arrange ---
        mockSecurityContext("testuser");
        List<ConversationVO> conversations = Arrays.asList(conversationVO);
        when(conversationService.getUserConversations(eq("testuser"))).thenReturn(conversations);

        // --- Act ---
        ResponseEntity<ResponseVO<List<ConversationVO>>> response = conversationController.getUserConversations();

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<List<ConversationVO>> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals(conversations, body.getData());
        assertEquals(1, body.getData().size());

        verify(conversationService, times(1)).getUserConversations(eq("testuser"));
    }

    @Test
    void testCreateConversation_Success() throws Exception {
        // --- Arrange ---
        mockSecurityContext("testuser");
        when(conversationService.createConversation(eq("testuser"), any(ConversationCreateDTO.class)))
                .thenReturn(conversationVO);

        // --- Act ---
        ResponseEntity<ResponseVO<ConversationVO>> response = conversationController.createConversation(createDTO);

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<ConversationVO> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals(conversationVO, body.getData());

        verify(conversationService, times(1)).createConversation(eq("testuser"), eq(createDTO));
    }

    @Test
    void testGetConversation_Success() throws Exception {
        // --- Arrange ---
        mockSecurityContext("testuser");
        String conversationId = "CONV-123456789012";
        when(conversationService.getConversation(eq("testuser"), eq(conversationId))).thenReturn(conversationVO);

        // --- Act ---
        ResponseEntity<ResponseVO<ConversationVO>> response = conversationController.getConversation(conversationId);

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<ConversationVO> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals(conversationVO, body.getData());

        verify(conversationService, times(1)).getConversation(eq("testuser"), eq(conversationId));
    }

    @Test
    void testDeleteConversation_Success() throws Exception {
        // --- Arrange ---
        mockSecurityContext("testuser");
        String conversationId = "CONV-123456789012";
        doNothing().when(conversationService).deleteConversation(eq("testuser"), eq(conversationId));

        // --- Act ---
        ResponseEntity<ResponseVO<String>> response = conversationController.deleteConversation(conversationId);

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<String> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals("删除成功", body.getData());

        verify(conversationService, times(1)).deleteConversation(eq("testuser"), eq(conversationId));
    }

    @Test
    void testGetMessage_Success() throws Exception {
        // --- Arrange ---
        mockSecurityContext("testuser");
        String conversationId = "CONV-123456789012";
        when(conversationService.getMessage(eq("testuser"), eq(conversationId), any(MessageCreateDTO.class)))
                .thenReturn(messageVO);

        // --- Act ---
        ResponseEntity<ResponseVO<MessageVO>> response = conversationController.getMessage(conversationId, messageDTO);

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<MessageVO> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals(messageVO, body.getData());

        verify(conversationService, times(1)).getMessage(eq("testuser"), eq(conversationId), eq(messageDTO));
    }

    @Test
    void testGetStreamMessage_Success() throws Exception {
        // --- Arrange ---
        mockSecurityContext("testuser");
        String conversationId = "CONV-123456789012";
        when(conversationService.getStreamMessage(eq("testuser"), eq(conversationId), any(MessageCreateDTO.class)))
                .thenReturn(Flux.just("Hello", " World"));

        // --- Act ---
        Flux<org.springframework.http.codec.ServerSentEvent<String>> response = 
                conversationController.getStreamMessage(conversationId, messageDTO);

        // --- Assert ---
        assertNotNull(response);
        List<String> messages = response.map(sse -> sse.data()).collectList().block();
        assertNotNull(messages);
        assertEquals(2, messages.size());
        assertEquals("Hello", messages.get(0));
        assertEquals(" World", messages.get(1));

        verify(conversationService, times(1)).getStreamMessage(eq("testuser"), eq(conversationId), eq(messageDTO));
    }
}

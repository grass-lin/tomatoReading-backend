package com.tomato.tomato_mall.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private String testToken;

    @BeforeEach
    void setUp() {
        testToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTUxNjIzOTAyMn0.test";
    }

    // --- setAuthenticationCookie 方法测试 ---
    @Test
    void setAuthenticationCookie_Success() {
        // --- Arrange ---
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);

        // --- Act ---
        authenticationService.setAuthenticationCookie(testToken, response);

        // --- Assert ---
        verify(response, times(1)).addCookie(cookieCaptor.capture());
        
        Cookie capturedCookie = cookieCaptor.getValue();
        assertNotNull(capturedCookie);
        assertEquals("token", capturedCookie.getName());
        assertEquals(testToken, capturedCookie.getValue());
        assertEquals("/", capturedCookie.getPath());
        assertTrue(capturedCookie.isHttpOnly());
        assertEquals(86400, capturedCookie.getMaxAge()); // 24 hours
    }

    @Test
    void setAuthenticationCookie_WithNullToken() {
        // --- Arrange ---
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);

        // --- Act ---
        authenticationService.setAuthenticationCookie(null, response);

        // --- Assert ---
        verify(response, times(1)).addCookie(cookieCaptor.capture());
        
        Cookie capturedCookie = cookieCaptor.getValue();
        assertNotNull(capturedCookie);
        assertEquals("token", capturedCookie.getName());
        assertNull(capturedCookie.getValue());
        assertEquals("/", capturedCookie.getPath());
        assertTrue(capturedCookie.isHttpOnly());
        assertEquals(86400, capturedCookie.getMaxAge());
    }

    @Test
    void setAuthenticationCookie_WithEmptyToken() {
        // --- Arrange ---
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        String emptyToken = "";

        // --- Act ---
        authenticationService.setAuthenticationCookie(emptyToken, response);

        // --- Assert ---
        verify(response, times(1)).addCookie(cookieCaptor.capture());
        
        Cookie capturedCookie = cookieCaptor.getValue();
        assertNotNull(capturedCookie);
        assertEquals("token", capturedCookie.getName());
        assertEquals(emptyToken, capturedCookie.getValue());
        assertEquals("/", capturedCookie.getPath());
        assertTrue(capturedCookie.isHttpOnly());
        assertEquals(86400, capturedCookie.getMaxAge());
    }

    // --- clearAuthenticationCookie 方法测试 ---
    @Test
    void clearAuthenticationCookie_Success() {
        // --- Arrange ---
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);

        // --- Act ---
        authenticationService.clearAuthenticationCookie(response);

        // --- Assert ---
        verify(response, times(1)).addCookie(cookieCaptor.capture());
        
        Cookie capturedCookie = cookieCaptor.getValue();
        assertNotNull(capturedCookie);
        assertEquals("token", capturedCookie.getName());
        assertNull(capturedCookie.getValue());
        assertEquals("/", capturedCookie.getPath());
        assertTrue(capturedCookie.isHttpOnly());
        assertEquals(0, capturedCookie.getMaxAge()); // 过期的Cookie
    }

    @Test
    void clearAuthenticationCookie_VerifyInteraction() {
        // --- Act ---
        authenticationService.clearAuthenticationCookie(response);

        // --- Assert ---
        verify(response, times(1)).addCookie(any(Cookie.class));
        verifyNoMoreInteractions(response);
    }

    @Test
    void setAuthenticationCookie_VerifyInteraction() {
        // --- Act ---
        authenticationService.setAuthenticationCookie(testToken, response);

        // --- Assert ---
        verify(response, times(1)).addCookie(any(Cookie.class));
        verifyNoMoreInteractions(response);
    }

    @Test
    void setAndClearAuthenticationCookie_Sequence() {
        // --- Arrange ---
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);

        // --- Act ---
        // 先设置 Cookie
        authenticationService.setAuthenticationCookie(testToken, response);
        // 再清除 Cookie
        authenticationService.clearAuthenticationCookie(response);

        // --- Assert ---
        verify(response, times(2)).addCookie(cookieCaptor.capture());
        
        var capturedCookies = cookieCaptor.getAllValues();
        assertEquals(2, capturedCookies.size());
        
        // 验证设置的 Cookie
        Cookie setCookie = capturedCookies.get(0);
        assertEquals("token", setCookie.getName());
        assertEquals(testToken, setCookie.getValue());
        assertEquals(86400, setCookie.getMaxAge());
        
        // 验证清除的 Cookie
        Cookie clearCookie = capturedCookies.get(1);
        assertEquals("token", clearCookie.getName());
        assertNull(clearCookie.getValue());
        assertEquals(0, clearCookie.getMaxAge());
    }
}

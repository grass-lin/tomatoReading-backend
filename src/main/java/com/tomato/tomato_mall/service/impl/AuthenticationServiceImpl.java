package com.tomato.tomato_mall.service.impl;

import com.tomato.tomato_mall.service.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final String COOKIE_NAME = "token";
    private static final int COOKIE_MAX_AGE = 86400; // 24 hours
    
    @Override
    public void setAuthenticationCookie(String token, HttpServletResponse response) {
        Cookie cookie = new Cookie(COOKIE_NAME, token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(COOKIE_MAX_AGE);
        // cookie.setSecure(true); // Uncomment in production with HTTPS
        response.addCookie(cookie);
    }
    
    @Override
    public void clearAuthenticationCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(COOKIE_NAME, null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
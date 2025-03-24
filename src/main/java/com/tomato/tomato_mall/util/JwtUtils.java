package com.tomato.tomato_mall.util;

import com.tomato.tomato_mall.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

/**
 * JWT工具类
 * <p>
 * 提供JWT(JSON Web Token)令牌的创建、验证和解析功能。
 * 该工具类封装了所有与JWT相关的操作，包括：
 * - 生成包含用户身份信息的JWT令牌
 * - 验证JWT令牌的有效性
 * - 从有效的JWT令牌中提取认证信息
 * - 从JWT令牌中提取用户名
 * </p>
 * <p>
 * JWT令牌采用HMAC-SHA算法进行签名，确保令牌在传输过程中不被篡改。
 * 令牌包含了用户名和角色等关键信息，支持无状态的身份验证机制。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Component
public class JwtUtils {

    private final JwtProperties jwtProperties;
    private final SecretKey key;

    /**
     * 构造函数
     * <p>
     * 初始化JWT工具类，从配置属性中读取密钥并创建签名密钥对象。
     * 密钥使用BASE64解码后作为HMAC-SHA算法的密钥材料。
     * </p>
     *
     * @param jwtProperties JWT配置属性，包含密钥和过期时间等配置
     */
    public JwtUtils(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecret()));
    }

    /**
     * 生成JWT令牌
     * <p>
     * 根据用户名和角色创建JWT令牌。令牌包含以下信息：
     * - 主题(subject)：用户名
     * - 声明(claim)：用户角色
     * - 签发时间(issuedAt)：当前时间
     * - 过期时间(expiration)：当前时间加上配置的过期时长
     * </p>
     * <p>
     * 令牌使用HMAC-SHA算法签名，确保其不被篡改。
     * </p>
     *
     * @param username 用户名，作为令牌的主题
     * @param role 用户角色，存储为令牌的自定义声明
     * @return 生成的JWT令牌字符串
     */
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                .signWith(key)
                .compact();
    }

    /**
     * 验证JWT令牌有效性
     * <p>
     * 检查令牌是否有效，包括：
     * - 签名验证：确保令牌未被篡改
     * - 格式验证：确保令牌结构正确
     * </p>
     * <p>
     * 该方法会捕获所有与JWT相关的异常，并返回验证结果。
     * 注意：此方法不检查令牌是否过期，令牌过期会在验证签名时被自动检测。
     * </p>
     *
     * @param token 要验证的JWT令牌字符串
     * @return 如果令牌有效则返回true，否则返回false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 从JWT令牌中提取认证信息
     * <p>
     * 解析JWT令牌并创建Spring Security认证对象，包括：
     * - 提取用户名作为主体
     * - 提取角色并转换为授权信息
     * </p>
     * <p>
     * 生成的认证对象可以直接设置到Spring Security上下文中，用于后续的权限验证。
     * 角色会自动添加"ROLE_"前缀，符合Spring Security的角色命名约定。
     * </p>
     *
     * @param token 有效的JWT令牌字符串
     * @return Spring Security的认证对象
     * @throws JwtException 如果令牌无效或已过期
     * @throws IllegalArgumentException 如果令牌格式错误
     */
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();

        String username = claims.getSubject();
        String role = claims.get("role", String.class);
        
        List<SimpleGrantedAuthority> authorities = 
            List.of(new SimpleGrantedAuthority("ROLE_" + role));

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }
    
    /**
     * 从JWT令牌中提取用户名
     * <p>
     * 解析JWT令牌并返回其中的用户名(subject)。
     * 该方法在不需要完整认证对象，只需要获取用户标识的场景下使用。
     * </p>
     *
     * @param token 有效的JWT令牌字符串
     * @return 令牌中的用户名
     * @throws JwtException 如果令牌无效或已过期
     * @throws IllegalArgumentException 如果令牌格式错误
     */
    public String extractUsername(String token) {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }
}
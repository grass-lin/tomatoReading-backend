package com.tomato.tomato_mall.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MessageVO {
    private String id;
    private String role;
    private String content;
    private LocalDateTime createTime;
}
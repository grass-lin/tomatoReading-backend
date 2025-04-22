package com.tomato.tomato_mall.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class ConversationVO {
    private String id;
    private String title;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<MessageVO> messages;
}
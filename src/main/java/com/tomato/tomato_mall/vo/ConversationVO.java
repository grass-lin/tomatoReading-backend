package com.tomato.tomato_mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationVO {
    private Long id;
    private String title;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<MessageVO> messages;
}
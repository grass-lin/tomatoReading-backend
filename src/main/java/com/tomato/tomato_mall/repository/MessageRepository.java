package com.tomato.tomato_mall.repository;

import com.tomato.tomato_mall.entity.Conversation;
import com.tomato.tomato_mall.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 消息数据访问仓库
 * 
 * @author Team CBDDL
 * @version 1.0
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, String> {

    /**
     * 根据对话查询消息列表并按创建时间顺序排列
     */
    List<Message> findByConversationOrderByCreateTime(Conversation conversation);
}
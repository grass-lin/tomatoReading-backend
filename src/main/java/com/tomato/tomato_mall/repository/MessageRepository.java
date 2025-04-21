package com.tomato.tomato_mall.repository;

import com.tomato.tomato_mall.entity.Conversation;
import com.tomato.tomato_mall.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversationOrderByCreateTime(Conversation conversation);
}
package com.tomato.tomato_mall.repository;

import com.tomato.tomato_mall.entity.Conversation;
import com.tomato.tomato_mall.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findByUserOrderByUpdateTimeDesc(User user);
}
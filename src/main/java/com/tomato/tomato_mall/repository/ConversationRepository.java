package com.tomato.tomato_mall.repository;

import com.tomato.tomato_mall.entity.Conversation;
import com.tomato.tomato_mall.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 对话数据访问仓库
 * 
 * @author Team CBDDL
 * @version 1.0
 */
@Repository
public interface ConversationRepository extends JpaRepository<Conversation, String> {

    /**
     * 根据用户查询对话列表并按更新时间倒序排列
     */
    List<Conversation> findByUserOrderByUpdateTimeDesc(User user);
}
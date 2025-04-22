package com.tomato.tomato_mall.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LLMConfig {
    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory) {
        return chatClientBuilder.defaultSystem("""
                你是一个专业的在线实体书商城助手，名叫"番茄书城助手"，提供友好、准确的回复。
                回答要简洁明了，语气亲切。
                避免提供任何虚假信息，如有不确定的问题，请诚实回答不知道。
                不要回答与书籍推荐,解答,购买无关的问题。
                当遇到敏感话题时，礼貌地引导用户回到书籍相关问题。
                """).defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory)).build();
    }
}
package com.tomato.tomato_mall.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tomato.tomato_mall.tool.ProductTools;

@Configuration
public class LLMConfig {
  @Bean
  public ChatClient chatClient(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory, ProductTools productTools) {
    return chatClientBuilder
        .defaultSystem("""
            你是一个专业的在线实体书商城助手，名叫"番茄书城助手"，提供友好、准确的回复。
            回答要简洁明了，语气亲切。
            避免提供任何虚假信息，如有不确定的问题，请诚实回答不知道。
            不要回答与书籍推荐,解答,购买无关的问题。
            当遇到敏感话题时，礼貌地引导用户回到书籍相关问题。
            你可以使用工具来查询产品信息。
            对于关于产品问题，请优先使用相应的工具来获取最新信息，而不是猜测。
            请关注用户对话中的上下文指代, 例如“这本书, 第几本书”, 优先结合历史对话, 从上下文确定具体所指书籍。
            同时注意, 不要在回答中夹杂url链接或者图片引用, 不要使用markdown标记语法.
            """)
        .defaultTools(productTools)
        .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
        // debug 使用
        .defaultAdvisors(new SimpleLoggerAdvisor())
        .build();
  }
}
package com.tomato.tomato_mall.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tomato.tomato_mall.tool.ProductTools;

/**
 * 大语言模型配置类
 * <p>
 * 该类负责创建和配置聊天客户端(ChatClient)，用于处理用户与AI助手的对话交互。
 * 通过Spring配置管理，集成了聊天记忆、工具调用和日志记录等功能，
 * 为番茄书城提供智能客服支持。
 * </p>
 */
@Configuration
public class LLMConfig {

    /**
     * 创建聊天客户端实例
     * 
     * <p>
     * ChatClient是与大语言模型交互的核心类，配置了专业的书城助手角色，
     * 集成了产品查询工具、对话记忆和调试日志功能
     * </p>
     * 
     * @param chatClientBuilder 聊天客户端构建器
     * @param chatMemory        聊天记忆组件，用于维护对话上下文
     * @param productTools      产品工具，用于查询商品信息
     * @return ChatClient 配置完成的聊天客户端实例
     */
    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory,
            ProductTools productTools) {
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
                        """)
                .defaultTools(productTools)
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                // debug 使用
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }
}
package com.tomato.tomato_mall.tool;

import com.tomato.tomato_mall.service.StockpileService;
import com.tomato.tomato_mall.vo.StockpileVO;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

/**
 * 产品工具类
 * <p>
 * 为大模型提供产品相关的方法调用
 * </p>
 * 
 * @author Team CBDDL
 * @version 1.0
 */
@Component
public class ProductTools {

    private final StockpileService stockpileService;
    private final VectorStore vectorStore;
    private final Integer defaultLimit = 5;
    private final Double defaultSimilarityThreshold = 0.6;

    /**
     * 构造函数，通过依赖注入初始化服务
     * 
     * @param stockpileService 库存服务，用于获取商品库存信息
     * @param vectorStore 向量存储服务，用于执行语义搜索
     */
    public ProductTools(StockpileService stockpileService, VectorStore vectorStore) {
        this.stockpileService = stockpileService;
        this.vectorStore = vectorStore;
    }

    /**
     * 根据自然语言描述搜索相关书籍
     * <p>
     * 使用向量数据库进行语义搜索，根据用户的自然语言描述
     * 找到最相关的书籍列表，并附带库存信息
     * 支持各种搜索场景，如按主题、作者风格、类型等搜索
     * </p>
     * 
     * @param query 用户的书籍描述或需求，例如"推荐关于人工智能的科幻小说"
     * @param limit 返回的最大书籍数量，可选参数，默认为5
     * @return 返回搜索结果的字符串，包含书籍信息和库存状态
     *         如果未找到相关书籍，返回提示信息
     *         如果查询为空，返回错误提示
     */
    @Tool(description = """
            根据用户的自然语言描述获取相关的书籍列表.
            如有必要可以拓展搜索请求.
            例如: '推荐关于人工智能的科幻小说', '找一些风格类似村上春树的书'.
            返回找到的书籍列表, 可以通过 limit 指定最多返回数量, 默认最多返回 5 条.
            书籍信息包含 书籍id, 书名, 简介, 详情, 规格, 库存.
            """)
    public String searchSimilarBooks(
            @ToolParam(description = "用户的书籍描述或需求") String query,
            @ToolParam(description = "返回的最大书籍数量", required = false) Integer limit) {
        if (limit == null || limit <= 0) {
            limit = defaultLimit;
        }
        if (query == null || query.isBlank()) {
            return "搜索描述不能为空";
        }
        System.out.println("query: " + query);
        System.out.println("limit: " + limit);

        SearchRequest searchRequest = SearchRequest.builder().query(query).topK(limit)
                .similarityThreshold(defaultSimilarityThreshold).build();
        List<Document> similarDocuments = vectorStore.similaritySearch(searchRequest);

        if (similarDocuments == null || similarDocuments.isEmpty()) {
            return "没有找到与描述相关的书籍.";
        }

        StringBuilder sb = new StringBuilder("搜索结果：\n");
        for (Document document : similarDocuments) {
            sb.append(document.getText()).append("\n");
            long productId = Long.parseLong(String.valueOf(document.getMetadata().getOrDefault("id", "0")));
            if (productId == 0)
                continue;
            StockpileVO stockpileVO = stockpileService.getStockpileByProductId(productId);
            if (stockpileVO != null) {
                sb.append("库存总数: ").append(stockpileVO.getAmount()).append("\n");
                sb.append("冻结数量: ").append(stockpileVO.getFrozen()).append("\n");
            }
            sb.append("-------------------------\n");
        }
        System.out.println(sb.toString());
        return sb.toString();
    }
}

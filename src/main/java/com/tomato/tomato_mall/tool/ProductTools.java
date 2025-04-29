package com.tomato.tomato_mall.tool;

import com.tomato.tomato_mall.service.ProductService;
import com.tomato.tomato_mall.service.StockpileService;
import com.tomato.tomato_mall.util.JsonUtils;
import com.tomato.tomato_mall.vo.ProductVO;
import com.tomato.tomato_mall.vo.SpecificationVO;
import com.tomato.tomato_mall.vo.StockpileVO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

@Component
public class ProductTools {

    private final ProductService productService;
    private final StockpileService stockpileService;
    private final VectorStore vectorStore;

    public ProductTools(ProductService productService, StockpileService stockpileService, VectorStore vectorStore) {
        this.productService = productService;
        this.stockpileService = stockpileService;
        this.vectorStore = vectorStore;
    }

    @Tool(description = """
            根据用户的自然语言描述获取相关的书籍列表.
            例如: '推荐关于人工智能的科幻小说', '找一些风格类似村上春树的书'.
            返回找到的书籍列表, 为 <id, 标题> 键值对.
            最多返回5个结果.
            当用户指定想了解书籍详情请一定要进一步使用 getProductInfo 工具.
            """)
    public String searchSimilarBooks(@ToolParam(description = "用户的书籍描述或需求") String query) {
        if (query == null || query.isBlank()) {
            return "搜索描述不能为空";
        }
        SearchRequest searchRequest = SearchRequest.builder().query(query).topK(5).build();
        List<Document> similarDocuments = vectorStore.similaritySearch(searchRequest);

        if (similarDocuments == null || similarDocuments.isEmpty())
            return "没有找到与描述相关的书籍.";

        Map<Long, String> results = similarDocuments.stream()
                .collect(Collectors.toMap(
                        doc -> Long.parseLong(String.valueOf(doc.getMetadata().getOrDefault("id", -1))),
                        doc -> String.valueOf(doc.getMetadata().getOrDefault("title", "未知标题"))));

        return JsonUtils.toJson(results);
    }

    @Tool(description = """
            根据书籍id获取书籍详细信息，返回对象包含
            id, tile, price, description, detail, rate 字段以及 specifications 对象列表, 以及库存信息,
            specifications 对象包含 id, item, value, productId 字段
            """)
    public String getProductInfo(@ToolParam(description = "书籍ID") Long productId) {
        if (productId == null)
            return "书籍ID不能为空";

        ProductVO productVO = productService.getProductById(productId);
        if (productVO == null)
            return "书籍不存在";

        StockpileVO stockpileVO = stockpileService.getStockpileByProductId(productId);

        return formatProductInfo(productVO, stockpileVO);
    }

    private String formatProductInfo(ProductVO productVO, StockpileVO stockpileVO) {
        StringBuilder sb = new StringBuilder();
        sb.append("商品信息：\n");
        sb.append("  ID: ").append(productVO.getId()).append("\n");
        sb.append("  标题: ").append(productVO.getTitle()).append("\n");
        sb.append("  价格: ").append(productVO.getPrice()).append("\n");
        if (productVO.getRate() != null) {
            sb.append("  评分: ").append(String.format("%.1f", productVO.getRate())).append("/10.0\n");
        } else {
            sb.append("  评分: 暂无评分\n");
        }
        sb.append("  描述: ").append(productVO.getDescription()).append("\n");
        sb.append("  详情: ").append(productVO.getDetail()).append("\n");

        if (productVO.getSpecifications() != null && !productVO.getSpecifications().isEmpty()) {
            sb.append("  规格:\n");
            for (SpecificationVO spec : productVO.getSpecifications()) {
                sb.append("    - ").append(spec.getItem()).append(": ").append(spec.getValue()).append("\n");
            }
        } else {
            sb.append("  规格: 无可用规格信息\n");
        }

        if (stockpileVO != null) {
            sb.append("库存信息:\n");
            sb.append("  可售数量: ").append(stockpileVO.getAmount() != null ? stockpileVO.getAmount() : "未知").append("\n");
            sb.append("  冻结数量: ").append(stockpileVO.getFrozen() != null ? stockpileVO.getFrozen() : "未知").append("\n");
        } else {
            sb.append("库存信息: 无法获取库存信息\n");
        }

        return sb.toString();
    }
}

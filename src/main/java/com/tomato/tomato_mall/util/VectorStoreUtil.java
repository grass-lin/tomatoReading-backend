package com.tomato.tomato_mall.util;

import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.stereotype.Component;
import com.tomato.tomato_mall.entity.Product;

/**
 * 与向量数据库交互的工具类
 * 
 * @author Team CBDDL
 * @version 1.0
 */
@Component
public class VectorStoreUtil {

    private final VectorStore vectorStore;

    public VectorStoreUtil(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    /**
     * 将单个商品信息注入向量数据库
     * 
     * @param product 商品对象
     */
    public void addProductVector(Product product) {
        if (product == null || product.getId() == null)
            return;
        // 使用 DocumentConverter 转换
        List<Document> documents = DocumentConverter.convertToDocuments(List.of(product));
        vectorStore.add(documents);
    }

    /**
     * 从向量数据库中移除指定ID的商品信息
     * 
     * @param productId 商品ID
     */
    public void removeProductVector(Long productId) {
        if (productId == null)
            return;

        // 构建过滤器以按ID删除
        Filter.Expression expression = new Filter.Expression(
                Filter.ExpressionType.EQ,
                new Filter.Key("id"),
                new Filter.Value(productId.toString()));
        vectorStore.delete(expression);
    }

    /**
     * 更新向量数据库中的商品信息（先删除后添加）
     * 
     * @param product 商品对象
     */
    public void updateProductVector(Product product) {
        if (product == null || product.getId() == null)
            return;
        // 注意：某些 VectorStore 可能提供更高效的更新方法
        removeProductVector(product.getId());
        addProductVector(product);
    }
}
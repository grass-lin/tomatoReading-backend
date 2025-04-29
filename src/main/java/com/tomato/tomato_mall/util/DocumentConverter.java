package com.tomato.tomato_mall.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.document.Document;

import com.tomato.tomato_mall.entity.Product;
import com.tomato.tomato_mall.entity.Specification;

public class DocumentConverter {

  public static Document convertToDocument(Product product) {
    // 构建文档内容
    StringBuilder contentBuilder = new StringBuilder();
    contentBuilder.append("书籍id: ").append(product.getId()).append("\n");
    contentBuilder.append("书名: ").append(product.getTitle()).append("\n");
    if (product.getDescription() != null && !product.getDescription().isEmpty()) {
      contentBuilder.append("简介: ").append(product.getDescription()).append("\n");
    }
    if (product.getDetail() != null && !product.getDetail().isEmpty()) {
      contentBuilder.append("详情: ").append(product.getDetail()).append("\n");
    }
    if (product.getSpecifications() != null && !product.getSpecifications().isEmpty()) {
      contentBuilder.append("规格信息:\n");
      for (Specification spec : product.getSpecifications()) {
        contentBuilder.append("- ").append(spec.getItem()).append(": ").append(spec.getValue()).append("\n");
      }
    }
    // 为文档添加元数据，方便后续过滤
    Map<String, Object> metadata = new HashMap<>();
    metadata.put("id", product.getId().toString());
    metadata.put("title", product.getTitle());
    metadata.put("rate", product.getRate().toString());
    metadata.put("price", product.getPrice().toString());

    // 创建文档对象
    return new Document(contentBuilder.toString(), metadata);
  }

  public static List<Document> convertToDocuments(List<Product> products) {
    return products.stream()
        .map(DocumentConverter::convertToDocument)
        .collect(Collectors.toList());
  }
}
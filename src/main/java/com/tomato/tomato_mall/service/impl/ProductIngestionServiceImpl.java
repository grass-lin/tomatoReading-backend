package com.tomato.tomato_mall.service.impl;

import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.stereotype.Service;
import com.tomato.tomato_mall.entity.Product;
import com.tomato.tomato_mall.service.ProductIngestionService;
import com.tomato.tomato_mall.util.ProductDocumentConverter;

@Service
public class ProductIngestionServiceImpl implements ProductIngestionService {

  private final ProductDocumentConverter productDocumentConverter;
  private final VectorStore vectorStore;

  public ProductIngestionServiceImpl(
      ProductDocumentConverter productDocumentConverter,
      VectorStore vectorStore) {
    this.productDocumentConverter = productDocumentConverter;
    this.vectorStore = vectorStore;
  }

  @Override
  public void ingestProduct(Product product) {
    if (product == null || product.getId() == null)
      return;
    List<Document> documents = productDocumentConverter.convertToDocuments(List.of(product));
    vectorStore.add(documents);
  }

  @Override
  public void removeProduct(Long productId) {
    if (productId == null)
      return;

    Filter.Expression expression = new Filter.Expression(
        Filter.ExpressionType.EQ,
        new Filter.Key("id"),
        new Filter.Value(productId.toString()));
    vectorStore.delete(expression);
  }

  @Override
  public void updateProduct(Product product) {
    if (product == null || product.getId() == null)
      return;
    removeProduct(product.getId());
    ingestProduct(product);
  }
}

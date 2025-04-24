package com.tomato.tomato_mall.service;
import org.springframework.stereotype.Service;
import com.tomato.tomato_mall.entity.Product;

@Service
public interface ProductIngestionService {
  public void ingestProduct(Product product);

  public void updateProduct(Product product);

  public void removeProduct(Long productId);

}
package com.tomato.tomato_mall.tool;

import com.tomato.tomato_mall.service.ProductService;
import com.tomato.tomato_mall.service.StockpileService;
import com.tomato.tomato_mall.vo.ProductVO;
import com.tomato.tomato_mall.vo.StockpileVO;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ProductTools {

    private final ProductService productService;
    private final StockpileService stockpileService;

    public ProductTools(ProductService productService, StockpileService stockpileService) {
        this.productService = productService;
        this.stockpileService = stockpileService;
    }

    @Tool(description = "获取书籍列表. 返回 <id,title> 键值对")
    public Map<Long, String> getProductList() {
        List<ProductVO> products = productService.getAllProducts();
        return products.stream()
                .collect(Collectors.toMap(ProductVO::getId, ProductVO::getTitle));
    }

    @Tool(description = """
            获取书籍详细信息，返回对象包含
            id, tile, price, description, detail, rate 以及 specifications 字段,
            specifications 字段为 <item, value> 键值对
            """)
    public ProductVO getProductInfo(@ToolParam(description = "产品ID") Long productId) {
        return productService.getProductById(productId);
    }

    @Tool(description = "获取指定库存信息")
    public String getStockpileInfo(@ToolParam(description = "产品ID") Long productId) {
        StockpileVO stockpileVO = stockpileService.getStockpileByProductId(productId);
        return String.format("库存总数: %d, 冻结数量: %d", stockpileVO.getAmount(), stockpileVO.getFrozen());
    }
}

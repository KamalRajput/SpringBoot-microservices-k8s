package com.shopping.ProductService.service;

import com.shopping.ProductService.model.ProductRequest;
import com.shopping.ProductService.model.ProductResponse;

public interface ProductService {

    long addProduct(ProductRequest productRequest);

    ProductResponse getProductById(long productId);

    void reduceQuantity(long productId, long quantity);
}

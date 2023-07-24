package com.shopping.ProductService.service;

import com.shopping.ProductService.entity.Product;
import com.shopping.ProductService.exception.ProductServiceCustomException;
import com.shopping.ProductService.model.ProductRequest;
import com.shopping.ProductService.model.ProductResponse;
import com.shopping.ProductService.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;

    @Override
    public long addProduct(ProductRequest productRequest) {
       log.info("adding product");
       Product product = Product.builder().productName(productRequest.getName())
               .price(productRequest.getPrice())
               .quantity(productRequest.getQuantity())
               .build();
       productRepository.save(product);
       log.info("product created");
        return product.getProductId();
    }

    @Override
    public ProductResponse getProductById(long productId) {
        log.info("Get the product for productId :{}", productId);
        Product product = productRepository.findById(productId).
                orElseThrow(() -> new ProductServiceCustomException("Product with given id not found","PRODUCT_NOT_FOUND"));
        ProductResponse productResponse = new ProductResponse();
        copyProperties(product,productResponse);
        return productResponse;
    }

    @Override
    public void reduceQuantity(long productId, long quantity) {
        log.info("reduce quantity {} for Id: {}", quantity, productId);
        Product product = productRepository.findById(productId).
                orElseThrow(() -> new ProductServiceCustomException("Product with given id not found","PRODUCT_NOT_FOUND"));
        if(product.getQuantity()<quantity){
            throw new ProductServiceCustomException("Product doesnt have sufficient quantity available","INSUFFICIENT_QUANTITY");
        }
        product.setQuantity(product.getQuantity()-quantity);
        productRepository.save(product);
        log.info("Product quantity updated successfully post order");
    }

    @Override
    public long deleteProduct(long productId) {
        log.info("Deleting product with id :{}", productId);
        Product product = productRepository.findById(productId).
                orElseThrow(() -> new ProductServiceCustomException("Product with given id not found","PRODUCT_NOT_FOUND"));
        productRepository.delete(product);
        return product.getProductId();
    }
}

package com.ads.product.service;

import com.ads.product.dto.ProductRequest;
import com.ads.product.dto.ProductResponse;
import com.ads.product.exception.ProductNotFoundException;
import com.ads.product.model.Product;
import com.ads.product.model.ProductCategory;
import com.ads.product.repository.ProductCategoryRepository;
import com.ads.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;

    @Transactional
    public ProductResponse createProduct(ProductRequest req) {
        ProductCategory category = null;
        if (req.getCategoryId() != null) {
            category = categoryRepository.findById(req.getCategoryId()).orElse(null);
        }
        if (category == null && req.getCategoryName() != null) {
            category = categoryRepository.findByName(req.getCategoryName())
                    .orElseGet(() -> categoryRepository.save(
                            ProductCategory.builder().name(req.getCategoryName()).build()
                    ));
        }

        Product p = Product.builder()
                .name(req.getName())
                .price(req.getPrice())
                .stock(req.getStock())
                .category(category)
                .build();

        Product saved = productRepository.save(p);
        return map(saved);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return map(p);
    }

    private ProductResponse map(Product p) {
        Long catId = p.getCategory() != null ? p.getCategory().getId() : null;
        String catName = p.getCategory() != null ? p.getCategory().getName() : null;
        return new ProductResponse(p.getId(), p.getName(), p.getPrice(), p.getStock(), catId, catName);
    }
}

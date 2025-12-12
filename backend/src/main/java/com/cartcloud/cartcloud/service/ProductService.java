package com.cartcloud.cartcloud.service;

import com.cartcloud.cartcloud.model.Category;
import com.cartcloud.cartcloud.model.Product;
import com.cartcloud.cartcloud.repository.CategoryRepository;
import com.cartcloud.cartcloud.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class ProductService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  
  public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
    this.productRepository = productRepository;
    this.categoryRepository = categoryRepository;
  } 

  public List<Product> getAllProducts(){
    return productRepository.findAll();
  }

  public Optional<Product> getProductById(Long productId) {
    return productRepository.findById(productId);
  }
  public List<Product> getProductByCategory(Long categoryId){
    Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
    return categoryOpt
    .map(productRepository::findByCategory)
    .orElse(List.of());
  }

  public Product createProduct(Product product) {
    return productRepository.save(product);
  }

  public Product updateProduct(Long id, Product updated) {
    return productRepository.findById(id)
           .map(existing -> {
            existing.setName(updated.getName());
            existing.setDescription(updated.getDescription());
            existing.setPrice(updated.getPrice());
            existing.setCategory(updated.getCategory());
            existing.setImageUrl(updated.getImageUrl());
            return productRepository.save(existing);
           })
           .orElseThrow(() -> new RuntimeException("Product not found"));
  }

  public void deleteProduct(Long id) {
    productRepository.deleteById(id);
  }
  
}

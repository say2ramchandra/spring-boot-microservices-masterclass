package com.masterclass.service;

import com.masterclass.dto.ProductDTO;
import com.masterclass.exception.ResourceNotFoundException;
import com.masterclass.model.Product;
import com.masterclass.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Product business logic.
 * 
 * @Service - Marks this as a service component
 * @Transactional - Manages database transactions
 * 
 * Business Logic Layer:
 * - Contains business rules
 * - Coordinates between controller and repository
 * - Handles data transformations (Entity ↔ DTO)
 * - Manages transactions
 * 
 * @author Spring Boot Microservices Masterclass
 */
@Service
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    
    // Constructor injection (recommended)
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    /**
     * Get all products.
     * 
     * @return List of all products as DTOs
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get product by ID.
     * 
     * @param id Product ID
     * @return Product DTO
     * @throws ResourceNotFoundException if product not found
     */
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return convertToDTO(product);
    }
    
    /**
     * Create a new product.
     * 
     * @param productDTO Product data
     * @return Created product DTO
     */
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = convertToEntity(productDTO);
        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }
    
    /**
     * Update an existing product.
     * 
     * @param id Product ID
     * @param productDTO Updated product data
     * @return Updated product DTO
     * @throws ResourceNotFoundException if product not found
     */
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        // Update fields
        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setQuantity(productDTO.getQuantity());
        
        Product updatedProduct = productRepository.save(existingProduct);
        return convertToDTO(updatedProduct);
    }
    
    /**
     * Delete a product.
     * 
     * @param id Product ID
     * @throws ResourceNotFoundException if product not found
     */
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
    
    /**
     * Search products by name.
     * 
     * @param name Search term
     * @return List of matching products
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Find products in a price range.
     * 
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @return List of products in the range
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Find products with low stock.
     * 
     * @param threshold Quantity threshold
     * @return List of low-stock products
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> getLowStockProducts(Integer threshold) {
        return productRepository.findByQuantityLessThan(threshold).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Helper methods for Entity ↔ DTO conversion
    
    /**
     * Convert Product entity to DTO.
     */
    private ProductDTO convertToDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity()
        );
    }
    
    /**
     * Convert ProductDTO to entity.
     */
    private Product convertToEntity(ProductDTO dto) {
        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        return product;
    }
}

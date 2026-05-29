package com.erp.service;

import com.erp.repository.UnitOfMeasureRepository;
import com.erp.repository.GenericProductRepository;
import com.erp.repository.BranchRepository;
import com.erp.repository.SupplierRepository;
import com.erp.entity.UnitOfMeasure;
import com.erp.entity.GenericProduct;
import com.erp.entity.Branch;
import com.erp.entity.Supplier;

import com.erp.dto.CreateProductRequest;
import com.erp.dto.ProductResponse;
import com.erp.dto.UpdateProductRequest;
import com.erp.entity.*;
import com.erp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductInventoryRepository inventoryRepository;
    private final UnitOfMeasureRepository uomRepository;
    private final GenericProductRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final BranchRepository branchRepository;

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        log.info("Creating product: {}", request.getName());

        // Validate UOM exists
        UnitOfMeasure uom = uomRepository.findById(request.getUomId())
                .orElseThrow(() -> new RuntimeException("UOM not found with ID: " + request.getUomId()));

        // Get category if provided
        GenericProduct category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found with ID: " + request.getCategoryId()));
        }

        // Get supplier if provided
        Supplier supplier = null;
        if (request.getSupplierId() != null) {
            supplier = supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + request.getSupplierId()));
        }

        // Check if SKU already exists
        if (request.getSku() != null && productRepository.findBySku(request.getSku()).isPresent()) {
            throw new RuntimeException("Product with SKU " + request.getSku() + " already exists");
        }

        // Create product
        Product product = Product.builder()
                .name(request.getName())
                .sku(request.getSku())
                .description(request.getDescription())
                .barcode(request.getBarcode())
                .price(request.getPrice())
                .costPrice(request.getCostPrice())
                .unitOfMeasure(uom)
                .weightInGrams(request.getWeightInGrams())
                .isWeighted(request.getIsWeighted() != null ? request.getIsWeighted() : false)
                .category(category)
                .supplier(supplier)
                .reorderPoint(request.getReorderPoint() != null ? request.getReorderPoint() : 0)
                .taxRate(request.getTaxRate() != null ? request.getTaxRate() : new java.math.BigDecimal("0.15"))
                .isActive(true)
                .build();

        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with ID: {}", savedProduct.getId());

        return convertToResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findByIsActiveTrue(pageable)
                .map(this::convertToResponse);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
        return convertToResponse(product);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        log.info("Updating product with ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getCostPrice() != null) product.setCostPrice(request.getCostPrice());
        if (request.getReorderPoint() != null) product.setReorderPoint(request.getReorderPoint());
        if (request.getTaxRate() != null) product.setTaxRate(request.getTaxRate());

        // Update UOM if provided
        if (request.getUomId() != null) {
            UnitOfMeasure uom = uomRepository.findById(request.getUomId())
                    .orElseThrow(() -> new RuntimeException("UOM not found with ID: " + request.getUomId()));
            product.setUnitOfMeasure(uom);
        }

        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully with ID: {}", id);

        return convertToResponse(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        log.info("Soft deleting product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
        product.setIsActive(false);
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(String search, Pageable pageable) {
        return productRepository.searchProducts(search, pageable)
                .map(this::convertToResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdAndIsActiveTrue(categoryId, pageable)
                .map(this::convertToResponse);
    }

    private ProductResponse convertToResponse(Product product) {
        ProductResponse response = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .description(product.getDescription())
                .barcode(product.getBarcode())
                .price(product.getPrice())
                .costPrice(product.getCostPrice())
                .weightInGrams(product.getWeightInGrams())
                .isWeighted(product.getIsWeighted())
                .reorderPoint(product.getReorderPoint())
                .taxRate(product.getTaxRate())
                .isActive(product.getIsActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();

        // Add UOM info
        if (product.getUnitOfMeasure() != null) {
            response.setUomId(product.getUnitOfMeasure().getId());
            response.setUomCode(product.getUnitOfMeasure().getCode());
            response.setUomNameAr(product.getUnitOfMeasure().getNameAr());
            response.setUomNameEn(product.getUnitOfMeasure().getNameEn());
        }

        // Add Category info
        if (product.getCategory() != null) {
            response.setCategoryId(product.getCategory().getId());
            response.setCategoryName(product.getCategory().getName());
        }

        // Add Supplier info
        if (product.getSupplier() != null) {
            response.setSupplierId(product.getSupplier().getId());
            response.setSupplierName(product.getSupplier().getName());
        }

        return response;


    }
}
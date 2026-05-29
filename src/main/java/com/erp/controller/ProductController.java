package com.erp.controller;

import com.erp.dto.ApiResponse;
import com.erp.dto.CreateProductRequest;
import com.erp.dto.ProductResponse;
import com.erp.dto.UpdateProductRequest;
import com.erp.service.ProductService;
import com.erp.util.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final MessageService messageService;

    // ============================================
    // CREATE - POST
    // ============================================

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody CreateProductRequest request) {

        log.info("POST /api/products - Creating product: {}", request.getName());

        try {
            ProductResponse response = productService.createProduct(request);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ApiResponse.<ProductResponse>builder()
                            .success(true)
                            .message(messageService.get("product.created.success"))
                            .data(response)
                            .code(201)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to create product: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<ProductResponse>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    // ============================================
    // READ - GET
    // ============================================

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("GET /api/products - Fetching all products");

        try {
            Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<ProductResponse> products = productService.getAllProducts(pageable);

            return ResponseEntity.ok(
                    ApiResponse.<Page<ProductResponse>>builder()
                            .success(true)
                            .message(messageService.get("product.retrieved.success"))
                            .data(products)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to fetch products: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<Page<ProductResponse>>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id) {

        log.info("GET /api/products/{} - Fetching product", id);

        try {
            ProductResponse product = productService.getProductById(id);

            return ResponseEntity.ok(
                    ApiResponse.<ProductResponse>builder()
                            .success(true)
                            .message(messageService.get("product.retrieved.success"))
                            .data(product)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to fetch product {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.<ProductResponse>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(404)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> searchProducts(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("GET /api/products/search - Searching products with query: {}", query);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ProductResponse> products = productService.searchProducts(query, pageable);

            return ResponseEntity.ok(
                    ApiResponse.<Page<ProductResponse>>builder()
                            .success(true)
                            .message(messageService.get("product.search.success"))
                            .data(products)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Search failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<Page<ProductResponse>>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("GET /api/products/category/{} - Fetching products by category", categoryId);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ProductResponse> products = productService.getProductsByCategory(categoryId, pageable);

            return ResponseEntity.ok(
                    ApiResponse.<Page<ProductResponse>>builder()
                            .success(true)
                            .message(messageService.get("product.retrieved.success"))
                            .data(products)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to fetch products by category: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<Page<ProductResponse>>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    // ============================================
    // UPDATE - PUT
    // ============================================

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {

        log.info("PUT /api/products/{} - Updating product", id);

        try {
            ProductResponse response = productService.updateProduct(id, request);

            return ResponseEntity.ok(
                    ApiResponse.<ProductResponse>builder()
                            .success(true)
                            .message(messageService.get("product.updated.success"))
                            .data(response)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to update product {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<ProductResponse>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    // ============================================
    // DELETE
    // ============================================

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {

        log.info("DELETE /api/products/{} - Deleting product", id);

        try {
            productService.deleteProduct(id);

            return ResponseEntity.ok(
                    ApiResponse.<Void>builder()
                            .success(true)
                            .message(messageService.get("product.deleted.success"))
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to delete product {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.<Void>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(404)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }
}
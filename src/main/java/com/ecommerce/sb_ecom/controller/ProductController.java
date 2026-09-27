package com.ecommerce.sb_ecom.controller;


import com.ecommerce.sb_ecom.DTO.ProductPageResponse;
import com.ecommerce.sb_ecom.DTO.ProductRequest;
import com.ecommerce.sb_ecom.DTO.ProductResponse;
import com.ecommerce.sb_ecom.security.CurrentUser;
import com.ecommerce.sb_ecom.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CurrentUser currentUser;


//    GET THE SIGNED-IN SELLER'S OWN PRODUCTS (for the seller dashboard). Must be
//    mapped before /api/admin/products/{id} would ever be confused with it - it isn't,
//    since that one has no GET mapping, but "mine" as a path segment here reads clearly
//    either way.
    @GetMapping("/api/admin/products/mine")
    public ResponseEntity<ProductPageResponse> getMyProducts(@RequestParam(defaultValue = "0") Integer pageNumber ,
                                                              @RequestParam(defaultValue = "20") Integer pageSize){
        return new ResponseEntity<>(productService.getProductsForSeller(currentUser.sellerId(), pageNumber, pageSize) , HttpStatus.OK);
    }



//    GET ALL PRODUCTS UNDER A CERTAIN CATEGORY
    @GetMapping("/api/public/products")
    public ResponseEntity<ProductPageResponse> getAllProducts(@RequestParam(defaultValue = "0") Integer pageNumber , @RequestParam(defaultValue = "5") Integer pageSize){
        return new ResponseEntity<>(productService.getAllProducts(pageNumber , pageSize) , HttpStatus.OK);
    }

//    GET A SINGLE PRODUCT BY ID (needed for the product detail page - there was
//    previously no way to fetch one product without paging through the whole list)
    @GetMapping("/api/public/products/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable String id){
        return new ResponseEntity<>(productService.getProductById(id) , HttpStatus.OK);
    }



//    CREATE PRODUCT
    @PostMapping("/api/admin/products")
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest productRequest){
        // Force the product onto the CALLER's own seller profile rather than trusting
        // productRequest.sellerId from the client - otherwise any seller could create a
        // product under another seller's id just by putting it in the request body.
        // Admins may still create a product for a specific seller via that field.
        if (!currentUser.isAdmin()) {
            productRequest.setSellerId(currentUser.sellerId());
        }
        return new ResponseEntity<>(productService.createProduct(productRequest) , HttpStatus.CREATED);
    }



//    UPDATE PRODUCT
    @PatchMapping("/api/admin/products/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable String id , @RequestBody ProductRequest productRequest){
        currentUser.assertOwnsProduct(id);
        return new ResponseEntity<>(productService.updateProduct(id , productRequest) , HttpStatus.OK);
    }




//    DELETE PRODUCT
    @DeleteMapping("/api/admin/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id){
        currentUser.assertOwnsProduct(id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }


//    FIND PRODUCT BY NAME
    @GetMapping("/api/public/products/search")
    public ResponseEntity<ProductPageResponse> findProductByName(@RequestParam String name ,
                                                                 @RequestParam(defaultValue = "0") Integer pageNumber ,
                                                                 @RequestParam(defaultValue = "5") Integer pageSize ,
                                                                 @RequestParam(defaultValue = "price") String sortBy,
                                                                 @RequestParam(defaultValue = "asc") String sortDir
                                                                 )
    {
        return new ResponseEntity<>(productService.findByProductName(name , pageNumber , pageSize , sortBy , sortDir) , HttpStatus.OK);
    }



//    FIND PRODUCTS BY CATEGORY NAME
    @GetMapping("/api/public/products/category-name/search")
    public ResponseEntity<ProductPageResponse> findProductByCategoryName(@RequestParam String name ,
                                                                         @RequestParam(defaultValue = "0") Integer pageNumber ,
                                                                         @RequestParam(defaultValue = "5") Integer pageSize ,
                                                                         @RequestParam(defaultValue = "price") String sortBy ,
                                                                         @RequestParam(defaultValue = "asc") String sortDir
                                                                        )
    {
        return new ResponseEntity<>(productService.findProductByCategoryName(name , pageNumber , pageSize , sortBy , sortDir) , HttpStatus.OK);
    }



}

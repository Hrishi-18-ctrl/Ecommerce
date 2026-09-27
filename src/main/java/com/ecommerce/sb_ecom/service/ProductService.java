package com.ecommerce.sb_ecom.service;

import com.ecommerce.sb_ecom.DTO.ProductPageResponse;
import com.ecommerce.sb_ecom.DTO.ProductRequest;
import com.ecommerce.sb_ecom.DTO.ProductResponse;
import com.ecommerce.sb_ecom.exceptions.CategoryNotFoundException;
import com.ecommerce.sb_ecom.exceptions.ProductNotFoundException;
import com.ecommerce.sb_ecom.exceptions.SellerNotFoundException;
import com.ecommerce.sb_ecom.model.Category;
import com.ecommerce.sb_ecom.model.Product;
import com.ecommerce.sb_ecom.model.Seller;
import com.ecommerce.sb_ecom.repository.CategoryRepository;
import com.ecommerce.sb_ecom.repository.ProductRepository;
import com.ecommerce.sb_ecom.repository.SellerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SellerRepository sellerRepository;
    private final ModelMapper modelMapper;


//    GET ALL PRODUCTS
    public ProductPageResponse getAllProducts(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber , pageSize);

        Page<Product> page = productRepository.findAll(pageable);

        List<Product> productList = page.getContent().stream().toList();

        List<ProductResponse> products = new ArrayList<>();

        for(Product product:productList){
            products.add(toResponse(product));
        }

        ProductPageResponse response = new ProductPageResponse();
        response.setContent(products);
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalPages(page.getTotalPages());
        response.setTotalElements(page.getTotalElements());
        response.setLast(page.isLast());

        return response;
    }


//    GET A SINGLE PRODUCT
    public ProductResponse getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("product with id " + id + " not found"));
        return toResponse(product);
    }


//    GET A SELLER'S OWN PRODUCTS (seller dashboard listing)
    public ProductPageResponse getProductsForSeller(String sellerId, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Product> page = productRepository.findBySellerId(pageable, sellerId);

        List<ProductResponse> products = page.getContent().stream().map(this::toResponse).toList();

        ProductPageResponse response = new ProductPageResponse();
        response.setContent(products);
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalPages(page.getTotalPages());
        response.setTotalElements(page.getTotalElements());
        response.setLast(page.isLast());
        return response;
    }


//    CREATE PRODUCT
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(
                        "Category with id " + request.getCategoryId() + " not found"));

        Product product = new Product();

        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());
        product.setStockQuantity(request.getStockQuantity());

        product.setCategory(category);


        Seller seller = sellerRepository.findById(request.getSellerId())
                .orElseThrow(() ->new SellerNotFoundException("seller with id " + request.getSellerId() + " not found"));

        product.setSeller(seller);

        Product saved = productRepository.save(product);

        return toResponse(saved);
    }




//    UPDATE PRODUCT
    @Transactional
    public ProductResponse updateProduct(String id, ProductRequest productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id " + id));

        if(productRequest.getName() != null){
            product.setName(productRequest.getName());
        }

        if(productRequest.getPrice() != null){
            product.setPrice(productRequest.getPrice());
        }

        if(productRequest.getStockQuantity() != null){
            product.setStockQuantity(productRequest.getStockQuantity());
        }

        if(productRequest.getImageUrl() != null){
            product.setImageUrl(productRequest.getImageUrl());
        }

        if(productRequest.getDescription() != null){
            product.setDescription(productRequest.getDescription());
        }

        if(productRequest.getCategoryId() != null){
            Category category = categoryRepository.findById(productRequest.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Category with id " + productRequest.getCategoryId() + " doesn't exist"));
            product.setCategory(category);
        }

        return toResponse(product);
    }





    //   DELETE PRODUCT
    @Transactional
    public void deleteProduct(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("product with id " + id + " not found"));
         productRepository.delete(product);
    }




    //    FIND PRODUCT BY NAME
    public ProductPageResponse findByProductName(String name , Integer pageNumber , Integer pageSize , String sortBy , String sortDir){

            Sort sort;
            if(sortDir.equalsIgnoreCase("asc")){
                sort = Sort.by(sortBy).ascending();
            }else{
                sort = Sort.by(sortBy).descending();
            }

            Pageable pageable = PageRequest.of(pageNumber , pageSize , sort);
            // Was findByName (exact, case-sensitive match) - see ProductRepository.
            Page<Product> page = productRepository.findByNameContainingIgnoreCase(pageable , name);


            List<ProductResponse> productResponseList = new ArrayList<>();
            List<Product> products = page.getContent().stream().toList();


            for(Product product:products){
                productResponseList.add(toResponse(product));
            }


            ProductPageResponse response = new ProductPageResponse();
            response.setContent(productResponseList);
            response.setPageNumber(page.getNumber());
            response.setPageSize(page.getSize());
            response.setTotalPages(page.getTotalPages());
            response.setTotalElements(page.getTotalElements());
            response.setLast(page.isLast());

            return response;
        }




//  FIND PRODUCTS BY CATEGORY NAME
    public ProductPageResponse findProductByCategoryName(String name , Integer pageNumber , Integer pageSize , String sortBy, String sortDir){

        Sort sort;
        if(sortDir.equalsIgnoreCase("asc")){
            sort = Sort.by(sortBy).ascending();
        }else{
            sort = Sort.by(sortBy).descending();
        }

        Optional<Category> category = categoryRepository.findByName(name);

        if(category.isEmpty()){
            throw new CategoryNotFoundException("category with name "+ name + " not found");
        }

        Pageable pageable = PageRequest.of(pageNumber , pageSize , sort);
        Page<Product> page = productRepository.findByCategoryId(pageable , category.get().getId());

        List<Product> products = page.getContent().stream().toList();
        List<ProductResponse> productResponseList = new ArrayList<>();

        for(Product product:products){
            productResponseList.add(toResponse(product));
        }

        ProductPageResponse response = new ProductPageResponse();
        response.setContent(productResponseList);
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalPages(page.getTotalPages());
        response.setTotalElements(page.getTotalElements());
        response.setLast(page.isLast());

        return response;
    }

    private ProductResponse toResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setImageUrl(product.getImageUrl());
        response.setCategory_id(product.getCategory().getId());
        response.setSellerId(product.getSeller().getId());
        response.setStockQuantity(product.getStockQuantity());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
    }
}

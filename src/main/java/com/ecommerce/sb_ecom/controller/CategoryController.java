package com.ecommerce.sb_ecom.controller;


import com.ecommerce.sb_ecom.DTO.CategoryPageResponse;
import com.ecommerce.sb_ecom.DTO.CategoryRequest;
import com.ecommerce.sb_ecom.DTO.CategoryResponse;
import com.ecommerce.sb_ecom.model.Category;
import com.ecommerce.sb_ecom.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

//  GET ALL CATEGORIES
    @GetMapping("/api/public/categories")
    public ResponseEntity<CategoryPageResponse> getAllCategories(@RequestParam(defaultValue = "0") Integer pageNumber , @RequestParam(defaultValue = "5") Integer pageSize){
        return new ResponseEntity<>(categoryService.getAllCategories(pageNumber , pageSize), HttpStatus.OK);
    }

//  CREATE NEW CATEGORY
    @PostMapping("/api/admin/categories")
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryRequest categoryRequest){
        return new ResponseEntity<>(categoryService.createCategory(categoryRequest), HttpStatus.CREATED);
    }


//  UPDATE CATEGORY
    @PatchMapping("/api/admin/categories/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable String id , @RequestBody CategoryRequest categoryRequest){
        return new ResponseEntity<>(categoryService.updateCategory(id, categoryRequest) , HttpStatus.OK);
    }



//    DELETE CATEGORY
    @DeleteMapping("/api/admin/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id){
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }


}

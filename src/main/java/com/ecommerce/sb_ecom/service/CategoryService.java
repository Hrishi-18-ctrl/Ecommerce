package com.ecommerce.sb_ecom.service;

import com.ecommerce.sb_ecom.DTO.CategoryPageResponse;
import com.ecommerce.sb_ecom.DTO.CategoryRequest;
import com.ecommerce.sb_ecom.DTO.CategoryResponse;
import com.ecommerce.sb_ecom.exceptions.CategoryNotFoundException;
import com.ecommerce.sb_ecom.model.Category;
import com.ecommerce.sb_ecom.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;



//    GET ALL CATEGORIES
    public CategoryPageResponse getAllCategories(Integer pageNumber , Integer pageSize){
        Pageable pageable = PageRequest.of(pageNumber , pageSize);

        Page<Category> page = categoryRepository.findAll(pageable);

        List<CategoryResponse> categories = page.getContent()
                .stream()
                .map(category -> modelMapper.map(category , CategoryResponse.class))
                .toList();

        CategoryPageResponse response = new CategoryPageResponse();
        response.setContent(categories);
            response.setPageNumber(page.getNumber());
            response.setPageSize(page.getSize());
            response.setTotalElements(page.getTotalElements());
            response.setTotalPages(page.getTotalPages());
            response.setLast(page.isLast());

            return response;
    }




//    CREATE CATEGORY
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {

        System.out.println(categoryRequest.getName());
            Category category = modelMapper.map(categoryRequest , Category.class);
            categoryRepository.save(category);
            CategoryResponse categoryResponse = modelMapper.map(category , CategoryResponse.class);
            return categoryResponse;
    }



//    UPDATE CATEGORY
    @Transactional
    public CategoryResponse updateCategory(String id , CategoryRequest categoryRequest) {
        Category existingCategory = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
        existingCategory.setName(categoryRequest.getName());
        return modelMapper.map(existingCategory , CategoryResponse.class);
    }



//    DELETE CATEGORY
    @Transactional
    public void deleteCategory(String id) {
        Category existingCategory = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
        categoryRepository.delete(existingCategory);
    }
}

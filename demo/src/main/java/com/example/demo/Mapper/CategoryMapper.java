package com.example.demo.Mapper;

import com.example.demo.DTO.CategoryDTO;
import com.example.demo.DummyObject.Category;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    @Autowired
    private ModelMapper modelMapper;

    public CategoryDTO mapCategoryToDTO(Category categoryToMap) {

        CategoryDTO newDTO = modelMapper.map(categoryToMap, CategoryDTO.class);

        return newDTO;
    }

    public static void mapPropertiesToCategory(Category sourceCategory, Category targetCategory) {

        targetCategory.setCategoryDescription(sourceCategory.getCategoryDescription());
        targetCategory.setCategoryName(sourceCategory.getCategoryName());
        targetCategory.setCategoryPopularity(sourceCategory.getCategoryPopularity());
        targetCategory.setProjectsCategories(sourceCategory.getProjectsCategories());

    }
}

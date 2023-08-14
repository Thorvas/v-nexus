package com.example.demo.Category;

import com.example.demo.Error.CategoryNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsible for category operations
 *
 * @author Thorvas
 */
@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Returns list of existing categories
     *
     * @return List of categories
     */
    public List<Category> searchCategories() {

        return categoryRepository.findAll();
    }

    /**
     * Deletes category
     *
     * @param category Provided category to delete
     */
    public void deleteCategory(Category category) {

        categoryRepository.delete(category);
    }

    /**
     * Searches for category within database
     *
     * @param id Id value of searched category
     * @return Optional containing result of search
     */
    public Category findCategory(Long id) {

        if (categoryRepository.findById(id).isPresent()) {
            return categoryRepository.findById(id).get();
        }

        throw new CategoryNotFoundException("Requested category could not be found.");
    }

    /**
     * Updated category in database
     *
     * @param sourceCategory Category containing old values
     * @param categoryDTO    Category containing new values
     * @return Updated category object
     */
    public Category updateCategory(Category sourceCategory, CategoryDTO categoryDTO) {

        Category category = modelMapper.map(categoryDTO, Category.class);

        category.setId(sourceCategory.getId());
        categoryRepository.save(category);

        return category;
    }

    /**
     * Creates category from DTO
     *
     * @param categoryDTO DTO object containing values to create
     * @return Newly created category object
     */
    public Category createCategory(CategoryDTO categoryDTO) {

        Category category = Category.builder()
                .categoryName(categoryDTO.getCategoryName())
                .categoryDescription(categoryDTO.getCategoryDescription())
                .categoryPopularity(categoryDTO.getCategoryPopularity())
                .build();

        categoryRepository.save(category);

        return category;
    }
}

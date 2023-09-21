package com.example.demo.Category;

import com.example.demo.Error.CategoryNotFoundException;
import com.example.demo.Error.CollectionEmptyException;
import com.example.demo.Error.InsufficientPermissionsException;
import com.example.demo.Project.ProjectDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
    CategoryServiceFacade categoryServiceFacade;

    /**
     * Returns list of existing categories
     *
     * @return List of categories
     */
    public List<CategoryDTO> searchCategories() {

        List<Category> categories = categoryRepository.findAll();

        if (!categories.isEmpty()) {

            return categories.stream()
                    .map(categoryServiceFacade::mapCategoryToDTO)
                    .collect(Collectors.toList());
        }

        throw new CollectionEmptyException("There are no categories in database");
    }

    /**
     * Deletes category
     *
     * @param categoryId Id value of deleted category
     * @return Deleted category
     */
    public CategoryDTO deleteCategory(Long categoryId) {

        Category category = this.findCategory(categoryId);

        if (categoryServiceFacade.checkIfAdmin(categoryServiceFacade.getLoggedVolunteer())) {

            categoryRepository.delete(category);

            return categoryServiceFacade.mapCategoryToDTO(category);
        }

        throw new InsufficientPermissionsException("You cannot delete categories because you are not an administrator");
    }

    /**
     * Searches for category by id parameter using utility method
     *
     * @param id Id value of searched category
     * @return Found category
     */
    public CategoryDTO searchCategory(Long id) {

        return categoryServiceFacade.mapCategoryToDTO(this.findCategory(id));
    }

    /**
     * Retrieves projects associated with category
     *
     * @param categoryId Id value of category
     * @return List of projects associated with category
     */
    public List<ProjectDTO> retrieveProjectsFromCategory(Long categoryId) {

        Category category = this.findCategory(categoryId);

        return category.getProjectsCategories().stream()
                .map(categoryServiceFacade::mapProjectToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Searches for category within database
     *
     * @param id Id value of searched category
     * @return Found category
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
     * @param categoryId  Id value of category
     * @param categoryDTO Category containing new values
     * @return Updated category object
     */
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {

        Category sourceCategory = this.findCategory(categoryId);

        if (categoryServiceFacade.checkIfAdmin(categoryServiceFacade.getLoggedVolunteer())) {

            categoryServiceFacade.mapDTOToCategory(categoryDTO, sourceCategory);

            categoryRepository.save(sourceCategory);

            return categoryServiceFacade.mapCategoryToDTO(sourceCategory);
        }

        throw new InsufficientPermissionsException("You are not permitted to update categories");
    }

    /**
     * Creates category from DTO
     *
     * @param categoryDTO DTO object containing values to create
     * @return Newly created category object
     */
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {

        if (categoryServiceFacade.checkIfAdmin(categoryServiceFacade.getLoggedVolunteer())) {

            Category category = Category.builder()
                    .categoryName(categoryDTO.getCategoryName())
                    .categoryDescription(categoryDTO.getCategoryDescription())
                    .categoryPopularity(categoryDTO.getCategoryPopularity())
                    .build();

            categoryRepository.save(category);

            return categoryServiceFacade.mapCategoryToDTO(category);
        }

        throw new InsufficientPermissionsException("You are not permitted to create new categories");
    }
}

package com.example.demo.DTO;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Data
@Relation(collectionRelation = "categories", itemRelation = "category")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class CategoryDTO extends RepresentationModel<CategoryDTO> {

    private Long id;

    @Size(min = 3, message = "Category name should have at least 3 characters")
    @NotBlank(message = "Category name cannot be empty.")
    @JsonProperty(value = "name")
    private String categoryName;

    @Size(min = 5, message = "Category description should have at least 5 characters")
    @NotBlank(message = "Category description cannot be empty.")
    @JsonProperty(value = "description")
    private String categoryDescription;

    @Min(value = 0)
    @NotNull(message = "Popularity cannot be empty")
    @JsonProperty(value = "popularity")
    private Integer categoryPopularity;
}

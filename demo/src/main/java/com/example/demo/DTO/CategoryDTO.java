package com.example.demo.DTO;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
    private String categoryName;
    private String categoryDescription;
    private Integer categoryPopularity;
}

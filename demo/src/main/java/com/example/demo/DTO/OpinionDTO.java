package com.example.demo.DTO;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Data
@Relation(collectionRelation = "opinions", itemRelation = "opinion")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class OpinionDTO extends RepresentationModel<OpinionDTO> {

    private Long id;

    @Size(min = 4, message = "Opinion content should be at least 4 characters long.")
    @NotBlank(message = "Opinion content should not be empty.")
    @JsonProperty(value = "content")
    private String content;

}

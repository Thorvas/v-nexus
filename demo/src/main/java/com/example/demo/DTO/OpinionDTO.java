package com.example.demo.DTO;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
    private String opinion;

}

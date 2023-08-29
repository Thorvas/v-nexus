package com.example.demo.Request;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

/**
 * Data Transfer Object for requests
 *
 * @author Thorvas
 */
@Data
@Relation(collectionRelation = "requests", itemRelation = "request")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class RequestDTO extends RepresentationModel<RequestDTO> {

    private Long id;

    @JsonProperty(value = "status")
    private RequestStatus status;
}

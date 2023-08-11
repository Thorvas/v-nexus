package com.example.demo.Opinion;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Mapper for opinions
 *
 * @author Thorvas
 */
@Component
public class OpinionMapper {

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Method that maps given opinion into its DTO with HATEOAS links
     *
     * @param opinionToMap Opinion that is to be mapped into DTO
     * @return Mapped opinionDTO object
     */
    public OpinionDTO mapOpinionToDTO(Opinion opinionToMap) {

        OpinionDTO newDTO = modelMapper.map(opinionToMap, OpinionDTO.class);

        Link opinionAuthorLink = linkTo(methodOn(OpinionController.class)
                .getAuthor(opinionToMap.getId())).withRel("opinion-author");

        Link describedProjectLink = linkTo(methodOn(OpinionController.class)
                .getProject(opinionToMap.getId())).withRel("described-project");

        Link selfLink = linkTo(methodOn(OpinionController.class)
                .getOpinion(newDTO.getId())).withSelfRel();

        newDTO.add(opinionAuthorLink, describedProjectLink, selfLink);

        return newDTO;
    }


}

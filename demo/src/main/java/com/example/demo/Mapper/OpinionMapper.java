package com.example.demo.Mapper;

import com.example.demo.Controller.OpinionController;
import com.example.demo.DTO.OpinionDTO;
import com.example.demo.Objects.Opinion;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OpinionMapper {

    @Autowired
    private ModelMapper modelMapper;

    public OpinionDTO mapOpinionToDTO(Opinion opinionToMap) {

        OpinionDTO newDTO = modelMapper.map(opinionToMap, OpinionDTO.class);

        Link opinionAuthorLink = linkTo(methodOn(OpinionController.class)
                .getAuthor(opinionToMap.getId())).withRel("opinion-author");

        Link describedProjectLink = linkTo(methodOn(OpinionController.class)
                .getProject(opinionToMap.getId())).withRel("described-project");

        Link selfLink = linkTo(methodOn(OpinionController.class)
                .getOpinion(newDTO.getId())).withSelfRel();

        Link rootLink = linkTo(methodOn(OpinionController.class)
                .getAllOpinions()).withRel("root");

        newDTO.add(opinionAuthorLink, describedProjectLink, selfLink, rootLink);

        return newDTO;
    }


}

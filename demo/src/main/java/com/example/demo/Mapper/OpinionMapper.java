package com.example.demo.Mapper;

import com.example.demo.Controller.OpinionController;
import com.example.demo.Controller.ProjectController;
import com.example.demo.Controller.VolunteerController;
import com.example.demo.DTO.OpinionDTO;
import com.example.demo.DummyObject.Opinion;
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

        newDTO.setAuthor(linkTo(methodOn(OpinionController.class)
                .getAuthor(opinionToMap.getId())).withRel("opinion-author"));

        newDTO.setDescribedProject(linkTo(methodOn(OpinionController.class)
                .getProject(opinionToMap.getId())).withRel("described-project"));

        return newDTO;
    }

    public static void mapPropertiesToOpinion(Opinion sourceOpinion, Opinion targetOpinion) {

        targetOpinion.setOpinion(sourceOpinion.getOpinion());
        targetOpinion.setAuthor(sourceOpinion.getAuthor());
        targetOpinion.setDescribedProject(sourceOpinion.getDescribedProject());

    }
}

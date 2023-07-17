package com.example.demo.Mapper;

import com.example.demo.DTO.OpinionDTO;
import com.example.demo.DummyObject.Opinion;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpinionMapper {

    @Autowired
    private ModelMapper modelMapper;

    public OpinionDTO mapOpinionToDTO(Opinion opinionToMap) {

        OpinionDTO newDTO = modelMapper.map(opinionToMap, OpinionDTO.class);

        return newDTO;
    }

    public static void mapPropertiesToOpinion(Opinion sourceOpinion, Opinion targetOpinion) {

        targetOpinion.setOpinion(sourceOpinion.getOpinion());
        targetOpinion.setAuthor(sourceOpinion.getAuthor());
        targetOpinion.setDescribedProject(sourceOpinion.getDescribedProject());

    }
}

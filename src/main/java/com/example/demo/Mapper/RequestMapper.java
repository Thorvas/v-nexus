package com.example.demo.Mapper;


import com.example.demo.Controller.RequestController;
import com.example.demo.DTO.RequestDTO;
import com.example.demo.Objects.VolunteerRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Mapper for requests
 *
 * @author Thorvas
 */
@Component
public class RequestMapper {

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Method that maps given request into its DTO with HATEOAS links
     *
     * @param request Request that is to be mapped into DTO
     * @return Mapped requestDTO object
     */
    public RequestDTO mapRequestToDTO(VolunteerRequest request) {

        RequestDTO requestDTO = modelMapper.map(request, RequestDTO.class);

        Link volunteerReceiverLink = linkTo(methodOn(RequestController.class)
                .getRequestReceiver(request.getRequestReceiver().getId()))
                .withRel("volunteer-receiver");

        Link volunteerSenderLink = linkTo(methodOn(RequestController.class)
                .getRequestSender(request.getRequestSender().getId()))
                .withRel("volunteer-sender");

        Link requestedProjectLink = linkTo(methodOn(RequestController.class)
                .getRequestProject(request.getRequestedProject().getId()))
                .withRel("requested-project");

        Link selfLink = linkTo(methodOn(RequestController.class)
                .getSpecificRequest(request.getId()))
                .withSelfRel();

        requestDTO.add(volunteerSenderLink, volunteerReceiverLink, requestedProjectLink, selfLink);

        return requestDTO;
    }
}

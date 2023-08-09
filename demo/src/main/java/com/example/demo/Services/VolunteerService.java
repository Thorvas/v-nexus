package com.example.demo.Services;

import com.example.demo.DTO.VolunteerDTO;
import com.example.demo.Objects.CustomUserDetails;
import com.example.demo.Objects.Volunteer;
import com.example.demo.Repositories.VolunteerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VolunteerService {

    @Autowired
    private VolunteerRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    public Volunteer getLoggedVolunteer() {

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userDetails.getUserData().getReferencedVolunteer();
    }

    public void updateInterests(Volunteer volunteer, List<String> interests) {

        volunteer.setInterests(interests);
    }

    public List<Volunteer> searchVolunteers() {

        return repository.findAll();
    }

    public Optional<Volunteer> findVolunteer(Long id) {

        return repository.findById(id);
    }

    public Volunteer saveVolunteer(Volunteer volunteer) {
        return repository.save(volunteer);
    }

    public boolean isMatchingVolunteer(Volunteer foundVolunteer, Volunteer user) {

        return foundVolunteer.getId().equals(user.getId());
    }

    public void deleteVolunteer(Volunteer volunteer) {
        repository.delete(volunteer);
    }

    public Volunteer updateVolunteer(Volunteer sourceVolunteer, VolunteerDTO volunteerDTO) {

        Volunteer volunteer = modelMapper.map(volunteerDTO, Volunteer.class);

        volunteer.setId(sourceVolunteer.getId());
        repository.save(volunteer);

        return volunteer;
    }
}

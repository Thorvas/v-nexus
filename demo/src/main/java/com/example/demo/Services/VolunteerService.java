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

/**
 * Service responsible for volunteer operations
 *
 * @author Thorvas
 */
@Service
public class VolunteerService {

    @Autowired
    private VolunteerRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Returns currently logged volunteer
     *
     * @return Volunteer that is currently logged in SecurityContext
     */
    public Volunteer getLoggedVolunteer() {

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userDetails.getUserData().getReferencedVolunteer();
    }

    /**
     * Updates interests of volunteer
     *
     * @param volunteer Updated volunteer
     * @param interests Interests that will be contained within volunteer object
     */
    public void updateInterests(Volunteer volunteer, List<String> interests) {

        volunteer.setInterests(interests);
    }

    /**
     * Searches for existing volunteers
     *
     * @return List of found volunteers
     */
    public List<Volunteer> searchVolunteers() {

        return repository.findAll();
    }

    /**
     * Searches for individual volunteer based on id parameter
     *
     * @param id Id of searched volunteer
     * @return Optional containing result of search
     */
    public Optional<Volunteer> findVolunteer(Long id) {

        return repository.findById(id);
    }

    /**
     * Checks whether volunteers are matching themselves
     *
     * @param foundVolunteer First volunteer in comparison
     * @param user           Second volunteer in comparison
     * @return Boolean value containing result of comparison
     */
    public boolean isMatchingVolunteer(Volunteer foundVolunteer, Volunteer user) {

        return foundVolunteer.getId().equals(user.getId());
    }

    /**
     * Deletes volunteer
     *
     * @param volunteer Deleted volunteer
     */
    public void deleteVolunteer(Volunteer volunteer) {
        repository.delete(volunteer);
    }

    /**
     * Updates volunteer
     *
     * @param sourceVolunteer Volunteer object representing old data
     * @param volunteerDTO    VolunteerDTO object representing new data
     * @return Updated volunteer
     */
    public Volunteer updateVolunteer(Volunteer sourceVolunteer, VolunteerDTO volunteerDTO) {

        Volunteer volunteer = modelMapper.map(volunteerDTO, Volunteer.class);

        volunteer.setId(sourceVolunteer.getId());
        repository.save(volunteer);

        return volunteer;
    }
}

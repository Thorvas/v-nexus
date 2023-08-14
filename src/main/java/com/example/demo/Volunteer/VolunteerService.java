package com.example.demo.Volunteer;

import com.example.demo.Authentication.AuthenticationService;
import com.example.demo.Error.VolunteerNotFoundException;
import com.example.demo.Project.ProjectDTO;
import com.example.demo.Project.ProjectMapper;
import com.example.demo.User.CustomUserDetails;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private VolunteerMapper volunteerMapper;

    @Autowired
    private ProjectMapper projectMapper;

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
    public Optional<VolunteerDTO> updateInterests(Volunteer volunteer, List<String> interests) {

        if (this.isMatchingVolunteer(this.getLoggedVolunteer(), volunteer) || authenticationService.checkIfAdmin(this.getLoggedVolunteer())) {

            volunteer.setInterests(interests);

            VolunteerDTO volunteerDTO = volunteerMapper.mapVolunteerToDTO(volunteer);

            return Optional.of(volunteerDTO);
        }

        return Optional.empty();

    }

    /**
     * Searches for existing volunteers
     *
     * @return List of found volunteers
     */
    public Optional<List<VolunteerDTO>> searchVolunteers() {

        List<Volunteer> foundVolunteers = repository.findAll();

        if (!foundVolunteers.isEmpty()) {
            List<VolunteerDTO> volunteerDTOs = foundVolunteers.stream()
                    .map(volunteerMapper::mapVolunteerToDTO)
                    .collect(Collectors.toList());
            return Optional.of(volunteerDTOs);
        }

        return Optional.empty();
    }

    /**
     * Searches for individual volunteer based on id parameter
     *
     * @param id Id of searched volunteer
     * @return Optional containing result of search
     */
    public Volunteer findVolunteer(Long id) {

        if (repository.findById(id).isPresent()) {
            return repository.findById(id).get();
        }

        throw new VolunteerNotFoundException("Requested volunteer could not be found");
    }

    public Optional<VolunteerDTO> searchVolunteer(Long id) {

        if (this.findVolunteer(id).isPresent()) {
            VolunteerDTO volunteerDTO = volunteerMapper.mapVolunteerToDTO(this.findVolunteer(id).get());

            return Optional.of(volunteerDTO);
        } return Optional.empty();
    }

    public Optional<List<ProjectDTO>> getParticipatingProjects(Long id) {

        if (this.findVolunteer(id).isPresent()) {

            Volunteer volunteer = this.findVolunteer(id).get();

            List<ProjectDTO> projectDTOs = volunteer.getParticipatingProjects().stream()
                    .map(projectMapper::mapProjectToDTO)
                    .collect(Collectors.toList());

            return Optional.of(projectDTOs);
        }
        return Optional.empty();
    }

    public Optional<List<ProjectDTO>> getOwnedProjects(Long id) {

        if (this.findVolunteer(id).isPresent()) {

            Volunteer volunteer = this.findVolunteer(id).get();

            List<ProjectDTO> projectDTOs = volunteer.getOwnedProjects().stream()
                    .map(projectMapper::mapProjectToDTO)
                    .collect(Collectors.toList());

            return Optional.of(projectDTOs);
        }
        return Optional.empty();
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
    public Optional<VolunteerDTO> deleteVolunteer(Volunteer volunteer) {

        if (this.isMatchingVolunteer(this.getLoggedVolunteer(), volunteer) || authenticationService.checkIfAdmin(this.getLoggedVolunteer())) {

            VolunteerDTO volunteerDTO = volunteerMapper.mapVolunteerToDTO(volunteer);

            repository.delete(volunteer);

            return Optional.of(volunteerDTO);
        }

        return Optional.empty();

    }

    /**
     * Updates volunteer
     *
     * @param sourceVolunteer Volunteer object representing old data
     * @param volunteerRequest    VolunteerDTO object representing new data
     * @return Updated volunteer
     */
    public Optional<VolunteerDTO> updateVolunteer(Volunteer sourceVolunteer, VolunteerDTO volunteerRequest) {

        if (this.isMatchingVolunteer(sourceVolunteer, this.getLoggedVolunteer()) || authenticationService.checkIfAdmin(this.getLoggedVolunteer())) {
            Volunteer volunteer = modelMapper.map(volunteerRequest, Volunteer.class);

            volunteer.setId(sourceVolunteer.getId());

            VolunteerDTO volunteerDTO = volunteerMapper.mapVolunteerToDTO(volunteer);

            repository.save(volunteer);

            return Optional.of(volunteerDTO);
        }

        return Optional.empty();
    }
}

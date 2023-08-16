package com.example.demo.Volunteer;

import com.example.demo.Authentication.AuthenticationService;
import com.example.demo.Error.CollectionEmptyException;
import com.example.demo.Error.InsufficientPermissionsException;
import com.example.demo.Error.VolunteerNotFoundException;
import com.example.demo.Project.ProjectDTO;
import com.example.demo.Project.ProjectMapper;
import com.example.demo.User.CustomUserDetails;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
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
     * @param volunteerId - Id value of updated volunteer
     * @param interests   Interests that will be contained within volunteer object
     * @return VolunteerDTO object that contains updated volunteer
     */
    public VolunteerDTO updateInterests(Long volunteerId, List<String> interests) {

        Volunteer volunteer = this.findVolunteer(volunteerId);

        if (this.isMatchingVolunteer(this.getLoggedVolunteer(), volunteer) || authenticationService.checkIfAdmin(this.getLoggedVolunteer())) {

            volunteer.setInterests(interests);

            return volunteerMapper.mapVolunteerToDTO(volunteer);
        }

        throw new InsufficientPermissionsException("You are not permitted to modify this volunteer's interests");

    }

    /**
     * Searches for existing volunteers
     *
     * @return List of found volunteers
     */
    public List<VolunteerDTO> searchVolunteers() {

        List<Volunteer> foundVolunteers = repository.findAll();

        if (!foundVolunteers.isEmpty()) {
            return foundVolunteers.stream()
                    .map(volunteerMapper::mapVolunteerToDTO)
                    .collect(Collectors.toList());
        }

        throw new CollectionEmptyException("There are no volunteers in database");
    }

    /**
     * Searches for individual volunteer based on id parameter
     *
     * @param id Id of searched volunteer
     * @return Result of search containing found volunteer
     */
    public Volunteer findVolunteer(Long id) {

        if (repository.findById(id).isPresent()) {
            return repository.findById(id).get();
        }

        throw new VolunteerNotFoundException("Requested volunteer could not be found");
    }

    /**
     * Searches for individual volunteer using utility method
     *
     * @param id Id of searched volunteer
     * @return Result of search containing found volunteer
     */
    public VolunteerDTO searchVolunteer(Long id) {

        return volunteerMapper.mapVolunteerToDTO(this.findVolunteer(id));
    }

    /**
     * Searches for projects that volunteer participates in
     *
     * @param id Id value of inspected volunteer
     * @return List of projects associated with volunteer
     */
    public List<ProjectDTO> getParticipatingProjects(Long id) {

        Volunteer volunteer = this.findVolunteer(id);

        if (!volunteer.getParticipatingProjects().isEmpty()) {

            return volunteer.getParticipatingProjects().stream()
                    .map(projectMapper::mapProjectToDTO)
                    .collect(Collectors.toList());
        }

        throw new CollectionEmptyException("Volunteer is not participating in any project yet.");
    }

    /**
     * Searches for projects that volunteer owns
     *
     * @param id Id value of inspected volunteer
     * @return List of projects owned by volunteer
     */
    public List<ProjectDTO> getOwnedProjects(Long id) {

        Volunteer volunteer = this.findVolunteer(id);
        if (!volunteer.getOwnedProjects().isEmpty()) {

            return volunteer.getOwnedProjects().stream()
                    .map(projectMapper::mapProjectToDTO)
                    .collect(Collectors.toList());
        }
        throw new CollectionEmptyException("Volunteer does not own any projects");
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
     * @param volunteerId Id value of inspected volunteer
     * @return Deleted volunteer
     */
    public VolunteerDTO deleteVolunteer(Long volunteerId) {

        Volunteer volunteer = this.findVolunteer(volunteerId);

        if (this.isMatchingVolunteer(this.getLoggedVolunteer(), volunteer) || authenticationService.checkIfAdmin(this.getLoggedVolunteer())) {

            VolunteerDTO volunteerDTO = volunteerMapper.mapVolunteerToDTO(volunteer);

            repository.delete(volunteer);

            return volunteerDTO;
        }

        throw new InsufficientPermissionsException("You are not permitted to delete this volnteer.");

    }

    /**
     * Updates volunteer
     *
     * @param volunteerRequest VolunteerDTO object representing new data
     * @return Updated volunteer
     */
    public VolunteerDTO updateVolunteer(Long volunteerId, VolunteerDTO volunteerRequest) {

        Volunteer sourceVolunteer = this.findVolunteer(volunteerId);

        if (this.isMatchingVolunteer(sourceVolunteer, this.getLoggedVolunteer()) || authenticationService.checkIfAdmin(this.getLoggedVolunteer())) {
            Volunteer volunteer = modelMapper.map(volunteerRequest, Volunteer.class);

            volunteer.setId(sourceVolunteer.getId());

            VolunteerDTO volunteerDTO = volunteerMapper.mapVolunteerToDTO(volunteer);

            repository.save(volunteer);

            return volunteerDTO;
        }

        throw new InsufficientPermissionsException("You are not permitted to update this volunteer's data");
    }
}

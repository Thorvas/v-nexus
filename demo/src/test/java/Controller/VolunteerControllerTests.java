package Controller;

import com.example.demo.Controller.VolunteerController;
import com.example.demo.DTO.VolunteerDTO;
import com.example.demo.DummyObject.Project;
import com.example.demo.DummyObject.Volunteer;
import com.example.demo.Services.VolunteerService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class VolunteerControllerTests {

    private final String SETUP_NAME = "TEST_NAME";
    private final String SETUP_SURNAME = "TEST_SURNAME";
    private final Integer SETUP_REPUTATION = 10;
    private final Long SETUP_ID = 1L;

    private Volunteer volunteerToTest;


    @Mock
    VolunteerService volunteerService;

    @InjectMocks
    VolunteerController volunteerController;

    @BeforeEach
    public void eachSetUp() {
        this.volunteerToTest = new Volunteer();
        volunteerToTest.setId(SETUP_ID);
        volunteerToTest.setName(SETUP_NAME);
        volunteerToTest.setSurname(SETUP_SURNAME);
        volunteerToTest.setReputation(SETUP_REPUTATION);
    }

    //Positive cases

    @Test
    public void getListOfVolunteers_ShouldReturnOkStatus() {

        Mockito.when(volunteerService.searchVolunteers()).thenReturn(Arrays.asList(this.volunteerToTest));

        ResponseEntity<List<VolunteerDTO>> response = volunteerController.getVolunteers();

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());

    }

    @Test
    public void getListOfInterestsAndSkills_shouldReturnOkStatus() {

        List<String> exampleInterests = List.of("First interest", "Second interest", "Third interest");
        List<String> exampleSkills = List.of("First skill", "Second skill", "Third skill");

        this.volunteerToTest.setInterests(exampleInterests);
        this.volunteerToTest.setSkills(exampleSkills);

        Mockito.when(volunteerService.findVolunteer(volunteerToTest.getId())).thenReturn(Optional.of(volunteerToTest));

        ResponseEntity<List<String>> interestsResponse = volunteerController.getInterests(volunteerToTest.getId());
        ResponseEntity<List<String>> skillsResponse = volunteerController.getSkills(volunteerToTest.getId());

        Assertions.assertEquals(exampleInterests, interestsResponse.getBody());
        Assertions.assertEquals(exampleSkills, skillsResponse.getBody());
        Assertions.assertEquals(HttpStatus.OK, interestsResponse.getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, skillsResponse.getStatusCode());

    }

    @Test
    public void getListOfOwnedProjects_shouldReturnOkStatus() {

        Project firstProject = new Project();

        firstProject.setProjectName("Example Project");
        firstProject.setId(1L);
        firstProject.setOwnerVolunteer(volunteerToTest);

        volunteerToTest.setOwnedProjects(List.of(firstProject));

        Mockito.when(volunteerService.findVolunteer(volunteerToTest.getId())).thenReturn(Optional.of(volunteerToTest));

        ResponseEntity<List<Project>> response = volunteerController.getOwnedProjects(volunteerToTest.getId());

        Assertions.assertEquals(Set.of(firstProject), response.getBody());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    public void getListOfParticipatingProjects_shouldReturnOkStatus() {

        Project firstProject = new Project();
        Project secondProject = new Project();
        Project thirdProject = new Project();

        Volunteer secondExampleVolunteer = new Volunteer();

        firstProject.setId(1L);
        secondProject.setId(2L);
        thirdProject.setId(3L);

        secondExampleVolunteer.setId(2L);
        secondExampleVolunteer.setName(SETUP_NAME);
        secondExampleVolunteer.setSurname(SETUP_SURNAME);
        secondExampleVolunteer.setReputation(SETUP_REPUTATION);

        List<Project> listOfProjects = List.of(firstProject, secondProject, thirdProject);
        List<Volunteer> listOfVolunteers = List.of(volunteerToTest, secondExampleVolunteer);

        volunteerToTest.setParticipatingProjects(listOfProjects);
        firstProject.setProjectVolunteers(listOfVolunteers);

        Mockito.when(volunteerService.findVolunteer(volunteerToTest.getId())).thenReturn(Optional.of(volunteerToTest));

        ResponseEntity<List<Project>> response = volunteerController.getProjects(volunteerToTest.getId());

        Assertions.assertEquals(listOfProjects, response.getBody());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    public void getVolunteer_ShouldReturnOkStatus() {
        Mockito.when(volunteerService.findVolunteer(this.volunteerToTest.getId())).thenReturn(Optional.of(this.volunteerToTest));

        ResponseEntity<VolunteerDTO> response = volunteerController.getVolunteer(volunteerToTest.getId());

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(this.volunteerToTest.getName(), response.getBody().getName());
        Assertions.assertEquals(this.volunteerToTest.getSurname(), response.getBody().getSurname());
        Assertions.assertEquals(this.volunteerToTest.getReputation(), response.getBody().getReputation());

    }

    @Test
    public void postVolunteer_ShouldReturnCreatedStatus() {

        Volunteer volunteerWithoutId = new Volunteer();

        volunteerWithoutId.setName(this.volunteerToTest.getName());
        volunteerWithoutId.setSurname(this.volunteerToTest.getSurname());
        volunteerWithoutId.setReputation(this.volunteerToTest.getReputation());

        Mockito.when(volunteerService.saveVolunteer(volunteerWithoutId)).thenReturn(this.volunteerToTest);

        ResponseEntity<Volunteer> response = volunteerController.postVolunteer(volunteerWithoutId);

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void updateVolunteer_ShouldReturnOkStatus() {

        final String UPDATED_NAME = "NEW_NAME";
        final String UPDATED_SURNAME = "NEW_SURNAME";
        final Integer UPDATED_REPUTATION = 10;

        Volunteer updatedVolunteer = new Volunteer();

        updatedVolunteer.setName(UPDATED_NAME);
        updatedVolunteer.setSurname(UPDATED_SURNAME);
        updatedVolunteer.setReputation(UPDATED_REPUTATION);

        Mockito.when(volunteerService.findVolunteer(this.volunteerToTest.getId())).thenReturn(Optional.of(this.volunteerToTest));

        ResponseEntity<VolunteerDTO> response = volunteerController.updateVolunteer(this.volunteerToTest.getId(), updatedVolunteer);

        Assertions.assertEquals(response.getBody().getName(), this.volunteerToTest.getName());
        Assertions.assertEquals(response.getBody().getSurname(), this.volunteerToTest.getSurname());
        Assertions.assertEquals(response.getBody().getReputation(), this.volunteerToTest.getReputation());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void deleteVolunteer_shouldReturnOkStatus() {

        Mockito.when(volunteerService.findVolunteer(this.volunteerToTest.getId())).thenReturn(Optional.of(this.volunteerToTest));

        ResponseEntity<VolunteerDTO> response = volunteerController.deleteEntity(this.volunteerToTest.getId());

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    //Negative cases

    @Test
    public void postVolunteer_shouldThrowIllegalArgumentException() {
        Volunteer invalidVolunteer = null;

        Assertions.assertThrows(IllegalArgumentException.class, () -> volunteerController.postVolunteer(invalidVolunteer));
    }

    @Test
    public void getListOfVolunteers_shouldThrowEntityNotFoundException() {

        Mockito.when(volunteerService.searchVolunteers()).thenReturn(null);

        Assertions.assertThrows(EntityNotFoundException.class, () -> volunteerController.getVolunteers());

    }

    @Test
    public void getVolunteer_shouldThrowEntityNotFoundException() {

        Mockito.when(volunteerService.findVolunteer(this.volunteerToTest.getId())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> volunteerController.getVolunteer(volunteerToTest.getId()));
    }

    @Test
    public void updateVolunteer_shouldThrowIllegalArgumentException() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> volunteerController.updateVolunteer(null, this.volunteerToTest));
    }

    @Test
    public void getListOfInterestsAndSkills_shouldThrowEntityNotFoundException() {

        Mockito.when(volunteerService.findVolunteer(this.volunteerToTest.getId())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> volunteerController.getSkills(volunteerToTest.getId()));
        Assertions.assertThrows(EntityNotFoundException.class, () -> volunteerController.getInterests(volunteerToTest.getId()));
    }
}

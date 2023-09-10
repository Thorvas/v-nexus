package Volunteer;

import com.example.demo.Configuration.ProjectConfiguration;
import com.example.demo.Configuration.SecurityConfig;
import com.example.demo.Error.CollectionEmptyException;
import com.example.demo.Project.Project;
import com.example.demo.Project.ProjectDTO;
import com.example.demo.Request.RequestController;
import com.example.demo.User.UserDetailsCustomImpl;
import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Volunteer.VolunteerController;
import com.example.demo.Volunteer.VolunteerDTO;
import com.example.demo.Volunteer.VolunteerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for VolunteerController
 *
 * @author Thorvas
 */
@WebMvcTest(controllers = VolunteerController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@Import(VolunteerController.class)
@ContextConfiguration(classes = {ProjectConfiguration.class})
public class VolunteerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VolunteerService volunteerService;

    private Volunteer volunteer;
    private Project project;

    private final Long volunteerId = 1L;
    private final Long projectId = 2L;

    @BeforeEach
    public void init() {

        volunteer = new Volunteer();
        project = new Project();

        volunteer.setId(volunteerId);
        project.setId(projectId);
    }

    public static String asJsonString(final Object obj) throws JsonProcessingException {

        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        final String json = objectMapper.writeValueAsString(obj);

        return json;
    }

    @WithMockUser
    @Test
    public void getAllVolunteers_shouldReturn200Status() throws Exception {

        VolunteerDTO volunteerDTO = new VolunteerDTO();
        volunteerDTO.setId(volunteerId);

        when(volunteerService.searchVolunteers()).thenReturn(List.of(volunteerDTO));

        mockMvc.perform(get("/api/v1/volunteers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.volunteers[0].id").value(volunteerId));
    }


    @WithMockUser
    @Test
    public void getVolunteerById_shouldReturn200Status() throws Exception {

        VolunteerDTO volunteerDTO = new VolunteerDTO();
        volunteerDTO.setId(volunteerId);

        when(volunteerService.searchVolunteer(volunteerId)).thenReturn(volunteerDTO);

        mockMvc.perform(get("/api/v1/volunteers/{id}", volunteerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(volunteerId));
    }

    @WithMockUser
    @Test
    public void getVolunteerProjects_shouldReturn200Status() throws Exception {

        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setId(project.getId());

        List<ProjectDTO> projectDTOs = Arrays.asList(projectDTO);

        volunteer.setParticipatingProjects(Arrays.asList(project));

        when(volunteerService.getParticipatingProjects(volunteerId)).thenReturn(projectDTOs);

        mockMvc.perform(get("/api/v1/volunteers/{id}/projects", volunteerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.projects[0].id").value(projectId));
    }

    @WithMockUser
    @Test
    public void getVolunteerOwnedProjects_shouldReturn200Status() throws Exception {

        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setId(project.getId());

        List<ProjectDTO> projectDTOs = Arrays.asList(projectDTO);

        volunteer.setOwnedProjects(Arrays.asList(project));

        when(volunteerService.getOwnedProjects(volunteerId)).thenReturn(projectDTOs);

        mockMvc.perform(get("/api/v1/volunteers/{id}/projects/owned", volunteerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.projects[0].id").value(projectId));
    }

    @WithMockUser
    @Test
    public void patchVolunteer_shouldReturn200Status() throws Exception {

        VolunteerDTO passedVolunteerDTO = new VolunteerDTO();
        passedVolunteerDTO.setName("John");
        passedVolunteerDTO.setContact("Example contact");
        passedVolunteerDTO.setSurname("DoeWithMinimumLength");
        passedVolunteerDTO.setReputation(50);
        passedVolunteerDTO.setDateOfBirth(LocalDate.of(2002, 12, 25));
        passedVolunteerDTO.setInterests(List.of("Example interest"));

        when(volunteerService.findVolunteer(volunteerId)).thenReturn(volunteer);
        when(volunteerService.updateVolunteer(volunteerId, passedVolunteerDTO)).thenReturn(passedVolunteerDTO);

        mockMvc.perform(patch("/api/v1/volunteers/{id}", volunteerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(passedVolunteerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John"));
    }

    @WithMockUser
    @Test
    public void deleteVolunteer_shouldReturn200Status() throws Exception {

        VolunteerDTO volunteerDTO = new VolunteerDTO();
        volunteerDTO.setId(volunteerId);

        when(volunteerService.deleteVolunteer(volunteerId)).thenReturn(volunteerDTO);

        mockMvc.perform(delete("/api/v1/volunteers/{id}", volunteerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(volunteerId));
    }

    @WithMockUser
    @Test
    public void addInterests_shouldReturn200Status() throws Exception {

        List<String> interests = List.of("Example interest one", "Example interest two");

        VolunteerDTO volunteerDTO = new VolunteerDTO();

        volunteerDTO.setId(volunteerId);

        when(volunteerService.updateInterests(volunteerId, interests)).thenReturn(volunteerDTO);

        mockMvc.perform(post("/api/v1/volunteers/{id}/interests", volunteerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(interests)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(volunteerId));
    }
}

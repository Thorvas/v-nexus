package Volunteer;

import com.example.demo.Configuration.ProjectConfiguration;
import com.example.demo.Project.Project;
import com.example.demo.Project.ProjectDTO;
import com.example.demo.Project.ProjectMapper;
import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Volunteer.VolunteerController;
import com.example.demo.Volunteer.VolunteerDTO;
import com.example.demo.Volunteer.VolunteerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for VolunteerController
 *
 * @author Thorvas
 */
@WebMvcTest(VolunteerController.class)
@Import(VolunteerController.class)
@ContextConfiguration(classes = ProjectConfiguration.class)
public class VolunteerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VolunteerService volunteerService;

    @Test
    @WithMockUser(authorities = {"AUTHORITY_ADMIN", "AUTHORITY_READ", "AUTHORITY_WRITE"})
    public void getAllVolunteers_shouldReturn200Status() throws Exception {

        mockMvc.perform(get("/api/v1/volunteers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(authorities = {"AUTHORITY_ADMIN", "AUTHORITY_READ", "AUTHORITY_WRITE"})
    public void getVolunteerById_shouldReturn200Status() throws Exception {

        Long volunteerId = 1L;
        VolunteerDTO volunteerDTO = new VolunteerDTO();
        volunteerDTO.setId(volunteerId);

        when(volunteerService.searchVolunteer(volunteerId)).thenReturn(volunteerDTO);

        mockMvc.perform(get("/api/v1/volunteers/{id}", volunteerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(volunteerId));
    }

    @Test
    @WithMockUser(authorities = {"AUTHORITY_ADMIN", "AUTHORITY_READ", "AUTHORITY_WRITE"})
    public void getVolunteerProjects_shouldReturn200Status() throws Exception {

        Volunteer volunteer = new Volunteer();
        Project project = new Project();
        ProjectDTO projectDTO = new ProjectDTO();

        Long projectId = 1L;
        Long volunteerId = 2L;

        project.setId(projectId);
        volunteer.setId(volunteerId);
        projectDTO.setId(project.getId());

        List<ProjectDTO> projectDTOs = Arrays.asList(projectDTO);

        volunteer.setParticipatingProjects(Arrays.asList(project));

        when(volunteerService.getParticipatingProjects(volunteerId)).thenReturn(projectDTOs);

        mockMvc.perform(get("/api/v1/volunteers/{id}/projects", volunteerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.projects[0].id").value(projectId));
    }
}

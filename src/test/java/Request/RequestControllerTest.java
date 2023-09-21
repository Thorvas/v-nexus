package Request;

import com.example.demo.Project.Project;
import com.example.demo.Project.ProjectDTO;
import com.example.demo.Request.RequestController;
import com.example.demo.Request.RequestDTO;
import com.example.demo.Request.RequestService;
import com.example.demo.Request.VolunteerRequest;
import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Volunteer.VolunteerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for RequestController
 *
 * @author Thorvas
 */
@WebMvcTest(controllers = RequestController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@Import(RequestController.class)
public class RequestControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestService requestService;

    private VolunteerRequest volunteerRequest;
    private Long requestId = 1L;

    @BeforeEach
    public void init() {

        volunteerRequest = new VolunteerRequest();
        volunteerRequest.setId(requestId);
    }

    @Test
    public void getAllRequests_shouldReturn200Status() throws Exception {

        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setId(requestId);

        when(requestService.searchAllRequests()).thenReturn(List.of(requestDTO));

        mockMvc.perform(get("/api/v1/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.requests[0].id").value(requestId));

    }

    @Test
    public void getRequest_shouldReturn200Status() throws Exception {

        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setId(requestId);

        when(requestService.searchRequest(requestId)).thenReturn(requestDTO);

        mockMvc.perform(get("/api/v1/requests/{id}", requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId));
    }

    @Test
    public void deleteRequest_shouldReturn200Status() throws Exception {

        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setId(requestId);

        when(requestService.deleteRequest(requestId)).thenReturn(requestDTO);

        mockMvc.perform(delete("/api/v1/requests/{id}", requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId));
    }

    @Test
    public void getVolunteerSender_shouldReturn200Status() throws Exception {

        Volunteer volunteer = new Volunteer();
        VolunteerDTO volunteerDTO = new VolunteerDTO();

        volunteer.setId(3L);
        volunteerDTO.setId(volunteer.getId());
        volunteerRequest.setRequestSender(volunteer);

        when(requestService.getRequestSender(requestId)).thenReturn(volunteerDTO);

        mockMvc.perform(get("/api/v1/requests/{id}/sender", requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(volunteer.getId()));
    }

    @Test
    public void getVolunteerReceiver_shouldReturn200Status() throws Exception {

        Volunteer volunteer = new Volunteer();
        VolunteerDTO volunteerDTO = new VolunteerDTO();

        volunteer.setId(3L);
        volunteerDTO.setId(volunteer.getId());
        volunteerRequest.setRequestReceiver(volunteer);

        when(requestService.getRequestReceiver(requestId)).thenReturn(volunteerDTO);

        mockMvc.perform(get("/api/v1/requests/{id}/receiver", requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(volunteer.getId()));
    }

    @Test
    public void getRequestedProject_shouldReturn200Status() throws Exception {

        Project project = new Project();
        ProjectDTO projectDTO = new ProjectDTO();

        project.setId(3L);
        projectDTO.setId(project.getId());

        when(requestService.getRequestedProject(requestId)).thenReturn(projectDTO);

        mockMvc.perform(get("/api/v1/requests/{id}/project", requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(project.getId()));
    }

    @Test
    public void acceptRequest_shouldReturn200Status() throws Exception {

        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setId(requestId);

        when(requestService.acceptRequest(requestId)).thenReturn(requestDTO);

        mockMvc.perform(patch("/api/v1/requests/{id}/accept", requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId));
    }

    @Test
    public void declineRequest_shouldReturn200Status() throws Exception {

        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setId(requestId);

        when(requestService.declineRequest(requestId)).thenReturn(requestDTO);

        mockMvc.perform(patch("/api/v1/requests/{id}/decline", requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId));
    }

}

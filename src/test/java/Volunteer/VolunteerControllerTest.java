package Volunteer;

import com.example.demo.Configuration.ProjectConfiguration;
import com.example.demo.Volunteer.VolunteerController;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

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

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/volunteers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }
}

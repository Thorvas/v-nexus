package com.example.demo.Volunteer;

import com.example.demo.ExceptionHandlers.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for VolunteerController
 *
 * @author Thorvas
 */
@WebMvcTest(controllers = VolunteerController.class)
@Import(VolunteerController.class)
@ContextConfiguration(classes = {VolunteerService.class})
@ImportAutoConfiguration(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
public class VolunteerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VolunteerServiceFacade volunteerServiceFacade;

    @MockBean
    private VolunteerRepository volunteerRepository;

    @Test
    public void getAllVolunteers_shouldReturn200Status() throws Exception {

        Volunteer volunteer = new Volunteer();
        volunteer.setName("John");

        VolunteerDTO volunteerDTO = new VolunteerDTO();
        volunteerDTO.setName("JohnDTO");

        when(volunteerRepository.findAll()).thenReturn(List.of(volunteer));
        when(volunteerServiceFacade.mapVolunteerToDTO(volunteer)).thenReturn(volunteerDTO);

        mockMvc.perform((get("/api/v1/volunteers")))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.volunteers[0].name").value("JohnDTO"))
                .andExpect(jsonPath("$._embedded.volunteers").isArray())
                .andExpect(jsonPath("$._embedded.volunteers").isNotEmpty());

        verify(volunteerRepository, times(1)).findAll();
        verify(volunteerServiceFacade, times(1)).mapVolunteerToDTO(volunteer);

    }
}

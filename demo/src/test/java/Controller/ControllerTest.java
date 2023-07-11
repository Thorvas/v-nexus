package Controller;

import com.example.demo.Controller.Controller;
import com.example.demo.DummyObject.DummyEntity;
import com.example.demo.Services.DummyEntityService;
import com.example.demo.Specification.SpecificationBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;


@ExtendWith(MockitoExtension.class)
public class ControllerTest {

    @Mock
    private DummyEntityService service;

    @InjectMocks
    private Controller controller;

    private DummyEntity dummyEntity;
    private final String SETUP_VOIVODESHIP_VALUE = "mazowieckie";

    private final Map<String, Integer> SETUP_POPULATION = Map.ofEntries(
            new AbstractMap.SimpleEntry<>("2012", 67678),
            new AbstractMap.SimpleEntry<>("2013", 67888)
    );
    private final Integer SETUP_ESTIMATED_POPULATION = 1100;
    private final String SETUP_NAME = "powiat aleksandrowski";

    private final LocalDate SETUP_DATE = LocalDate.of(2023, Month.JANUARY, 1);

    @BeforeEach
    public void setUp() {

        dummyEntity = new DummyEntity();
        dummyEntity.setId(1L);
        dummyEntity.setVoivodeship(SETUP_VOIVODESHIP_VALUE);
        dummyEntity.setLastUpdated(SETUP_DATE);
        dummyEntity.setName(SETUP_NAME);
        dummyEntity.setPrediction(SETUP_ESTIMATED_POPULATION);
        dummyEntity.setPopulation(SETUP_POPULATION);

    }

    @Test
    public void postEntity_shouldReturnCreatedStatus() {

        Mockito.when(service.findEntityForSaveOrUpdate(dummyEntity.getId())).thenReturn(Optional.empty());
        ResponseEntity<DummyEntity> response = controller.postEntity(dummyEntity);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals(dummyEntity, response.getBody());

    }

    @Test
    public void postEntity_shouldReturnOKStatus() {

        Mockito.when(service.findEntityForSaveOrUpdate(dummyEntity.getId())).thenReturn(Optional.of(dummyEntity));
        ResponseEntity<DummyEntity> response = controller.postEntity(dummyEntity);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(dummyEntity, response.getBody());

    }

    @Test
    public void postEntity_shouldReturnBadRequestStatus() {
        dummyEntity.setId(null);

        ResponseEntity<DummyEntity> response = controller.postEntity(dummyEntity);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    }

    @Test
    public void getEntity_shouldReturnCorrectObject() {
        Pageable pageable = PageRequest.of(0, 10);
        SpecificationBuilder specificationBuilder = new SpecificationBuilder();
        List<DummyEntity> entityToPage = Arrays.asList(dummyEntity);

        specificationBuilder.withName(dummyEntity.getName())
                .withVoivodeship(dummyEntity.getVoivodeship());

        Mockito.when(service.searchEntities(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(new PageImpl<>(entityToPage));

        ResponseEntity<Page<DummyEntity>> response = controller.getEstimation(dummyEntity.getVoivodeship(), dummyEntity.getName(), pageable);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    public void patchEntity_shouldReturnBadRequestStatus() {
        Mockito.when(service.findEntityById(dummyEntity.getId())).thenReturn(null);

    }
}

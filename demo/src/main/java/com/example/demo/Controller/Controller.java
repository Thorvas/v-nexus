package com.example.demo.Controller;

import com.example.demo.DummyObject.DummyEntity;
import com.example.demo.Mapper.DummyEntityMapper;
import com.example.demo.Services.DummyEntityService;
import com.example.demo.Specification.SpecificationBuilder;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controller for handling DummyEntity requests.
 *
 * @author Thorvas
 */
@RestController
@RequestMapping("/api")
public class Controller {

    @Autowired
    private DummyEntityService service;

    /**
     * Receives data from logic part of application and saves received data to database
     *
     * @param entity An entity sent to endpoint from data-analysis module which is saved to database
     * @return The ResponseEntity object which contains saved entity
     */
    @PostMapping(value = "/postEstimation", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DummyEntity> postEntity(@RequestBody @Valid DummyEntity entity) {

        if (entity.getId() != null) {

            Optional<DummyEntity> foundEntity = service.findEntityForSaveOrUpdate(entity.getId());

            if (foundEntity.isPresent()) {

                DummyEntity presentEntity = foundEntity.get();
                DummyEntityMapper.mapEntity(entity, presentEntity);
                service.saveEntity(presentEntity);
                return new ResponseEntity<>(entity, HttpStatus.OK);

            }

            service.saveEntity(entity);
            return new ResponseEntity<>(entity, HttpStatus.CREATED);

        }

        else {

            throw new IllegalArgumentException("An ID shouldn't be null.");
        }
    }

    /**
     * Retrieves estimation data from database based on parameters provided for filtering.
     *
     * @param voivodeship The voivodeship value (as String)
     * @param poviat      The poviat value (as String)
     * @param pageable    The Pageable object for pagination
     * @return The ResponseEntity containing result of search
     */
    @GetMapping(value = "/estimation", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<DummyEntity>> getEstimation(@RequestParam(value = "voivodeship", required = false) String voivodeship,
                                                           @RequestParam(value = "poviat", required = false) String poviat,
                                                           @PageableDefault(size = Integer.MAX_VALUE) Pageable pageable) {

        SpecificationBuilder specificationBuilder = new SpecificationBuilder();

        specificationBuilder.withName(poviat)
                .withVoivodeship(voivodeship);

        Page<DummyEntity> entities = service.searchEntities(specificationBuilder.buildSpecification(), pageable);

        return ResponseEntity.ok(entities);
    }

    /**
     * Updates an entity in database
     *
     * @param id     An ID value of updated object
     * @param entity New object to substitute an updated object
     * @return The ResponseEntity object containing updated object
     */
    @PatchMapping(value = "/entities/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DummyEntity> updateEntity(@PathVariable Long id, @RequestBody DummyEntity entity) {

        Optional<DummyEntity> editedEntity = service.findEntityById(id);

        if (editedEntity.isPresent()) {

            DummyEntityMapper.mapEntity(entity, editedEntity.get());
            DummyEntity savedEntity = service.saveEntity(editedEntity.get());
            return new ResponseEntity<>(savedEntity, HttpStatus.OK);

        } else {

            throw new EntityNotFoundException("Requested entity was not found.");
        }
    }

    /**
     * Deletes an entity from database
     *
     * @param id An ID value of deleted object
     * @return The String with deletion message
     */
    @DeleteMapping(value = "/entities/{id}")
    public ResponseEntity<String> deleteEntity(@PathVariable Long id) {

        if (id != null) {
            Optional<DummyEntity> entityToDelete = service.findEntityById(id);
            service.deleteEntity(entityToDelete.orElseThrow(() -> new EntityNotFoundException("An entity to delete was not found.")));
            return new ResponseEntity<>("An entity has been deleted.", HttpStatus.OK);
        }
        throw new IllegalArgumentException("An ID shouldn't be null.");


    }
}

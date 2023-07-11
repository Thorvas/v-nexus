package com.example.demo.Services;

import com.example.demo.DummyObject.DummyEntity;
import com.example.demo.Repositories.DummyEntityRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Dummy Entity service responsible for operations performed on DummyEntity objects
 */
@Service
@Transactional
public class DummyEntityService {
    @Autowired
    DummyEntityRepository repository;

    /**
     * Deletes an entity from database
     *
     * @param id An ID value of deleted entity
     */
    public void deleteEntity(DummyEntity entity) {

        repository.deleteById(entity.getId());
    }

    /**
     * Finds an entity by ID value
     *
     * @param id An ID value of found entity
     * @return Found DummyEntity object or new DummyEntity object if it was not found
     */
    public Optional<DummyEntity> findEntityById(Long id) {

        Optional<DummyEntity> entity = repository.findById(id);

        return entity;
    }

    /**
     * Finds an entity by provided specification
     *
     * @param specification Specification object which contains dynamic query
     * @param pageable      Pageable object used for pagination
     * @return Page object that contains paged data
     */
    public Page<DummyEntity> searchEntities(Specification<DummyEntity> specification, Pageable pageable) {

        return repository.findAll(specification, pageable);
    }

    /**
     * Saves an entity to database
     *
     * @param entity An object saved in database
     */
    public DummyEntity saveEntity(DummyEntity entity) {

        if (entity != null) {
            return repository.save(entity);
        } else {
            throw new EntityNotFoundException("An entity couldn't be saved, because it was null.");
        }
    }

    public Optional<DummyEntity> findEntityForSaveOrUpdate(Long id) {
        return repository.findById(id);
    }
}

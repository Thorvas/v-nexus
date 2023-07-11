package com.example.demo.Mapper;

import com.example.demo.DummyObject.DummyEntity;

/**
 * Mapper used in Controller to update DummyEntity object with new one
 *
 * @author Thorvas
 */
public class DummyEntityMapper {

    /**
     * Maps sourceEntity DummyEntity to targetEntity DummyEntity to update it in database
     *
     * @param sourceEntity A source entity providing new data to be updated
     * @param targetEntity A target entity which will be saved in database
     */
    public static void mapEntity(DummyEntity sourceEntity, DummyEntity targetEntity) {

        targetEntity.setPrediction(sourceEntity.getPrediction());
        targetEntity.setLastUpdated(sourceEntity.getLastUpdated());
        targetEntity.setPopulation(sourceEntity.getPopulation());
        targetEntity.setVoivodeship(sourceEntity.getVoivodeship());
    }
}

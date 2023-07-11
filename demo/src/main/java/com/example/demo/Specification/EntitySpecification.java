package com.example.demo.Specification;

import com.example.demo.DummyObject.DummyEntity;
import jakarta.persistence.criteria.MapJoin;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications for building dynamic queries
 */
public class EntitySpecification {

    public static Specification<DummyEntity> withVoivodeship(String voivodeship) {

        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("voivodeship"), voivodeship);
    }

    public static Specification<DummyEntity> withName(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("name"), name);
    }
}

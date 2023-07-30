package com.example.demo.Utility;

import jakarta.persistence.EntityNotFoundException;

import java.util.HashSet;
import java.util.Set;

public class CollectionsUtil {

    public static <T> Set<T> addElementToSet(Set<T> collection, T element) {

        if (collection == null) {

            collection = new HashSet<>();
        }
        collection.add(element);

        return collection;
    }

    public static <T> Set<T> removeElementFromSet(Set<T> collection, T element) {

        if (collection != null) {

            if (collection.contains(element)) {

                collection.remove(element);

                return collection;

            } else {

                throw new EntityNotFoundException("Collection does not contain that object.");
            }
        } else {

            throw new RuntimeException("There are no elements in this collection.");
        }
    }
}

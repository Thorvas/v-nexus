package com.example.demo.Utility;

import jakarta.persistence.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that makes adding elements to class collections easier
 *
 * @author Thorvas
 */
public class CollectionsUtil {

    public static <T> List<T> addElementToList(List<T> collection, T element) {

        if (collection == null) {

            collection = new ArrayList<>();
        }

        collection.add(element);

        return collection;
    }

    public static <T> List<T> removeElementFromList(List<T> collection, T element) {

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

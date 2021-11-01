package com.ess.udemyfreecoursesverifier.core;

import java.util.List;

public interface FirebaseCrud<T> {

    T save(T item);

    List<T> findAll();

    T findOneBy(String attribute, String value);

    List<T> findAllBy(String attribute, String value);
}

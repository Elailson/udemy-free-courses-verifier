package com.ess.udemyfreecoursesverifier.core;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public abstract class ServiceFirebase<T extends Model> implements FirebaseCrud<T> {

    private final CollectionReference collectionRef;
    private final Class<T> type;

    protected ServiceFirebase(Class<T> type, Firestore firestore, String path) {
        collectionRef = firestore.collection(path);
        this.type = type;
    }

    @Override
    public T save(T item) {
        ApiFuture<DocumentReference> apiFuture = collectionRef.add(item);
        try {
            String id = apiFuture.get().getId();
            item.setId(id);
            collectionRef.document(item.getId()).set(item);
            return item;
        } catch (ExecutionException | InterruptedException exception) {
            return null;
        }
    }

    @Override
    public List<T> findAll() {
        ApiFuture<QuerySnapshot> apiFuture = collectionRef.get();
        try {
            return apiFuture
                    .get()
                    .getDocuments()
                    .stream()
                    .map(document -> document.toObject(type))
                    .collect(Collectors.toList());
        } catch (ExecutionException | InterruptedException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public T findOneBy(String attribute, String value) {
        Query query = collectionRef.whereEqualTo(attribute, value);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        try {
            if (querySnapshot.get().getDocuments().isEmpty()) return null;
            DocumentSnapshot documentSnapshot = querySnapshot.get().getDocuments().get(0);
            return documentSnapshot.exists() ? documentSnapshot.toObject(type) : null;
        } catch (ExecutionException | InterruptedException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public List<T> findAllBy(String attribute, String value) {
        ApiFuture<QuerySnapshot> apiFuture = collectionRef.whereEqualTo(attribute, value).get();
        try {
            return apiFuture
                    .get()
                    .getDocuments()
                    .stream()
                    .map(document -> document.toObject(type))
                    .collect(Collectors.toList());
        } catch (ExecutionException | InterruptedException exception) {
            throw new RuntimeException(exception);
        }
    }
}

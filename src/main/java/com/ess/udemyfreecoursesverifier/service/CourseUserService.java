package com.ess.udemyfreecoursesverifier.service;

import com.ess.udemyfreecoursesverifier.core.ServiceFirebase;
import com.ess.udemyfreecoursesverifier.model.CourseUser;
import com.ess.udemyfreecoursesverifier.util.FirebaseUtil;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class CourseUserService extends ServiceFirebase<CourseUser> {

    private final CollectionReference userCourseReference;

    public CourseUserService() {
        super(CourseUser.class, FirestoreClient.getFirestore(FirebaseUtil.getInstance()), "user_link");
        this.userCourseReference = FirestoreClient.getFirestore(FirebaseUtil.getInstance()).collection("user_link");
    }

    public void setNotificado(CourseUser courseUser) {
        userCourseReference.document(courseUser.getId()).update("notificado", true);
    }

    public List<CourseUser> findAllNotificadoFalseByCourseId(String courseId) {
        ApiFuture<QuerySnapshot> apiFuture = userCourseReference
                .whereEqualTo("courseId", courseId)
                .whereEqualTo("notificado", false)
                .get();
        try {
            List<CourseUser> coursesUsers = apiFuture
                    .get()
                    .getDocuments()
                    .stream()
                    .map(queryDocumentSnapshot ->
                            queryDocumentSnapshot.toObject(CourseUser.class))
                    .collect(Collectors.toList());
            coursesUsers.forEach(this::setNotificado);
            return coursesUsers;
        } catch (ExecutionException | InterruptedException exception) {
            LoggerFactory.getLogger(this.getClass().getCanonicalName()).error("Erro ao tentar buscar todas as associações");
            throw new RuntimeException("Erro: " + exception.getMessage());
        }
    }
}

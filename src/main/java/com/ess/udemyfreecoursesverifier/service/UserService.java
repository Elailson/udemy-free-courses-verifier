package com.ess.udemyfreecoursesverifier.service;

import com.ess.udemyfreecoursesverifier.core.ServiceFirebase;
import com.ess.udemyfreecoursesverifier.model.Course;
import com.ess.udemyfreecoursesverifier.model.CourseUser;
import com.ess.udemyfreecoursesverifier.model.User;
import com.ess.udemyfreecoursesverifier.util.FirebaseUtil;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class UserService extends ServiceFirebase<User> {

    private final CollectionReference usersReference;
    private final CourseService courseService;
    private final CourseUserService courseUserService;

    public UserService() {
        super(User.class, FirestoreClient.getFirestore(FirebaseUtil.getInstance()), "users");
        this.usersReference = FirestoreClient.getFirestore(FirebaseUtil.getInstance()).collection("users");
        this.courseService = new CourseService();
        this.courseUserService = new CourseUserService();
    }

    public List<User> findAllByIds(List<String> usersId) {
        Query query = usersReference.whereIn("id", usersId);
        ApiFuture<QuerySnapshot> apiFuture = query.get();
        try {
            return apiFuture
                    .get()
                    .getDocuments()
                    .stream()
                    .map(document -> document.toObject(User.class))
                    .collect(Collectors.toList());
        } catch (ExecutionException | InterruptedException exception) {
            LoggerFactory.getLogger(this.getClass().getCanonicalName()).error("Erro ao tentar buscar todos os usuarios");
            throw new RuntimeException("Erro: " + exception.getMessage());
        }
    }

    public List<User> findAllByLink(String link) {
        Course course = courseService.findOneBy("link", link);
        List<CourseUser> coursesUsers = courseUserService.findAllNotificadoFalseByCourseId(course.getId());
        if (coursesUsers.isEmpty()) return new ArrayList<>();
        return findAllByIds(coursesUsers.stream()
                .map(CourseUser::getUserId)
                .collect(Collectors.toList()));
    }
}

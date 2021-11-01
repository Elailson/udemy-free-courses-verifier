package com.ess.udemyfreecoursesverifier.service;

import com.ess.udemyfreecoursesverifier.core.ServiceFirebase;
import com.ess.udemyfreecoursesverifier.model.Course;
import com.ess.udemyfreecoursesverifier.util.FirebaseUtil;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

@Service
public class CourseService extends ServiceFirebase<Course> {

    public CourseService() {
        super(Course.class, FirestoreClient.getFirestore(FirebaseUtil.getInstance()), "courses");
    }
}

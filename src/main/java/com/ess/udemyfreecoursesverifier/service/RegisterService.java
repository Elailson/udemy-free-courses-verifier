package com.ess.udemyfreecoursesverifier.service;

import com.ess.udemyfreecoursesverifier.exception.AlreadyExistsException;
import com.ess.udemyfreecoursesverifier.model.Course;
import com.ess.udemyfreecoursesverifier.model.CourseUser;
import com.ess.udemyfreecoursesverifier.model.Register;
import com.ess.udemyfreecoursesverifier.model.User;
import com.ess.udemyfreecoursesverifier.util.LinkTransform;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {

    private final CourseService courseService;
    private final UserService userService;
    private final CourseUserService courseUserService;

    public RegisterService() {
        this.courseService = new CourseService();
        this.userService = new UserService();
        this.courseUserService = new CourseUserService();
    }

    public void validate(Register register) {
        register.setLink(LinkTransform.removeSlashes(register.getLink()));
        Course course = courseService.findOneBy("link", register.getLink());
        User user = userService.findOneBy("email", register.getEmail());

        if (course != null && user != null) {
            throw new AlreadyExistsException("Seu email já está associado a esse curso.");
        }

        if (course == null) {
            Course newCourse = new Course();
            newCourse.setLink(register.getLink());
            course = courseService.save(newCourse);
        }

        if (user == null) {
            User newUser = new User();
            newUser.setEmail(register.getEmail());
            user = userService.save(newUser);
        }

        CourseUser courseUser = new CourseUser();
        courseUser.setUserId(user.getId());
        courseUser.setCourseId(course.getId());
        courseUser.setNotificado(false);
        courseUserService.save(courseUser);
    }

}

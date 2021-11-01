package com.ess.udemyfreecoursesverifier.model;

import com.ess.udemyfreecoursesverifier.core.Model;

public class CourseUser extends Model {

    private String courseId;
    private String userId;
    private boolean notificado;

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isNotificado() {
        return notificado;
    }

    public void setNotificado(boolean notificado) {
        this.notificado = notificado;
    }
}

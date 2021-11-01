package com.ess.udemyfreecoursesverifier.util;

public class LinkTransform {

    public static String removeSlashes(String link) {
        return link.replace("/", "__");
    }

    public static String addSlashes(String link) {
        return link.replace("__", "/");
    }
}

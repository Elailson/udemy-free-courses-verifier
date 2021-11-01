package com.ess.udemyfreecoursesverifier.util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FirebaseUtil {

    private static FirebaseApp firebaseApp;

    private FirebaseUtil() {
    }

    public static FirebaseApp getInstance() {
        if (firebaseApp == null) {
            firebaseApp = initialize();
        }

        return firebaseApp;
    }

    private static FirebaseApp initialize() {
        try {
            FileInputStream jsonFile = new FileInputStream(new FirebaseUtil().getConfigFile());
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(jsonFile))
                    .build();
            return FirebaseApp.initializeApp(options);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private String getConfigFile() {
        boolean isWindows = System.getProperty("os.name").contains("Windows");
        try {
            Properties properties = new Properties();
            InputStream inputStream = getClass()
                    .getClassLoader()
                    .getResourceAsStream("application.properties");
            properties.load(inputStream);
            return properties.getProperty(isWindows ? "json.path.windows" : "json.path.linux");
        } catch (IOException exception) {
            throw new RuntimeException("Não foi possível carregar arquivo");
        }
    }
}

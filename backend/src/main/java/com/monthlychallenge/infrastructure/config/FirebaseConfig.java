package com.monthlychallenge.infrastructure.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${app.notification.firebase.credentials-path}")
    private String credentialsPath;

    @PostConstruct
    public void init() {
        if ("none".equalsIgnoreCase(credentialsPath)) {
            log.warn("Firebase credentials path set to 'none' — push notifications disabled");
            return;
        }
        if (!FirebaseApp.getApps().isEmpty()) return;
        try {
            InputStream serviceAccount = loadCredentials();
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
            log.info("Firebase Admin SDK initialised");
        } catch (IOException e) {
            log.error("Failed to initialise Firebase Admin SDK: {}", e.getMessage());
        }
    }

    private InputStream loadCredentials() throws IOException {
        try {
            return new ClassPathResource(credentialsPath).getInputStream();
        } catch (IOException ex) {
            return new java.io.FileInputStream(credentialsPath);
        }
    }
}

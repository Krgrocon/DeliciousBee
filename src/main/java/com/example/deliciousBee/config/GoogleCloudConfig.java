package com.example.deliciousBee.config;
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.cloud.storage.Storage;
//import com.google.cloud.storage.StorageOptions;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//
//@Configuration
//public class GoogleCloudConfig {
//
//    @Bean
//    public Storage storage() throws IOException {
//        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("src/main/resources/deliciousbee-acb114448e3c.json"));
//        return StorageOptions.newBuilder().setCredentials(credentials).build().getService();
//    }
//}

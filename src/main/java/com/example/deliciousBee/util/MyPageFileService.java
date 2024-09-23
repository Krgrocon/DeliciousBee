package com.example.deliciousBee.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.deliciousBee.model.file.MyPageAttachedFile;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;


@Service
public class MyPageFileService {
	@Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    private final ResourceLoader resourceLoader;

    public MyPageFileService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public MyPageAttachedFile saveFile(MultipartFile mfile) throws IOException {
        if (mfile == null || mfile.isEmpty()) {
            return null;
        }

        // Google Cloud Storage 키 파일 설정
        String keyFileName = "deliciousbee-acb114448e3c.json";  // GCP 서비스 계정 키 파일명
        Resource resource = resourceLoader.getResource("classpath:" + keyFileName);
        InputStream keyFile = resource.getInputStream();

        // Google Cloud Storage 클라이언트 생성
        Storage storage = StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(keyFile))
                .build()
                .getService();

        // 원본 파일명
        String originalFilename = mfile.getOriginalFilename();

        // GCS에 저장할 고유 파일명 생성
        String savedFilename = UUID.randomUUID().toString() + "_" + originalFilename;

        // GCS에 파일 업로드
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, savedFilename)
                .setContentType(mfile.getContentType())
                .build();

        Blob blob = storage.create(blobInfo, mfile.getInputStream());

        // 업로드된 파일의 정보를 AttachedFile 객체로 반환
        return new  MyPageAttachedFile(originalFilename, savedFilename, mfile.getSize());
    }

    public boolean deleteFile(String savedFilename) {
        try {
            // Google Cloud Storage 클라이언트 생성 (같은 방식으로 생성)
            String keyFileName = "deliciousbee-acb114448e3c.json";  // GCP 서비스 계정 키 파일명
            Resource resource = resourceLoader.getResource("classpath:" + keyFileName);
            InputStream keyFile = resource.getInputStream();

            Storage storage = StorageOptions.newBuilder()
                    .setCredentials(GoogleCredentials.fromStream(keyFile))
                    .build()
                    .getService();

            // GCS에서 파일 삭제
            boolean deleted = storage.delete(bucketName, savedFilename);
            return deleted;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}


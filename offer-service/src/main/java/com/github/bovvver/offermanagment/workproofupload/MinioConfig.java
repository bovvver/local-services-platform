package com.github.bovvver.offermanagment.workproofupload;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class MinioConfig {

    @Value("${MINIO_ROOT_USER}")
    private String USERNAME;

    @Value("${MINIO_ROOT_PASSWORD}")
    private String PASSWORD;

    @Value("${MINIO_URL}")
    private String MINIO_URL;

    @Bean
    MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(MINIO_URL)
                .credentials(USERNAME, PASSWORD)
                .build();
    }
}

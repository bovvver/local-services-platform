package com.github.bovvver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import({TestSecurityConfig.class, TestMongoConfig.class})
abstract class BaseIntegrationTest {

    @Container
    static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @Autowired
    protected TestRestTemplate restTemplate;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        String connectionString = String.format(
                "mongodb://%s:%d/testdb",
                mongoDBContainer.getHost(),
                mongoDBContainer.getFirstMappedPort()
        );
        registry.add("spring.data.mongodb.uri", () -> connectionString);
    }

    @Test
    void connectionEstablished() {
        assertTrue(mongoDBContainer.isRunning());
        assertTrue(mongoDBContainer.isCreated());
    }
}

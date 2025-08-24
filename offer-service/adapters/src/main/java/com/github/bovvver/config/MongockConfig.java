package com.github.bovvver.config;

import io.mongock.driver.mongodb.springdata.v4.SpringDataMongoV4Driver;
import io.mongock.runner.springboot.MongockSpringboot;
import io.mongock.runner.springboot.base.MongockInitializingBeanRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Configuration class for Mongock, a database migration tool.
 * This class defines the bean responsible for initializing the Mongock runner.
 */
@Configuration
class MongockConfig {

    /**
     * Creates and configures a `MongockInitializingBeanRunner` bean.
     * This runner is responsible for executing database migrations using Mongock.
     *
     * @param springContext the Spring application context, used to integrate Mongock with Spring
     * @param mongoTemplate the `MongoTemplate` instance, used to interact with the MongoDB database
     * @return a configured `MongockInitializingBeanRunner` instance
     */
    @Bean
    public MongockInitializingBeanRunner mongockRunner(ApplicationContext springContext, MongoTemplate mongoTemplate) {
        return MongockSpringboot.builder()
                .setDriver(SpringDataMongoV4Driver.withDefaultLock(mongoTemplate))
                .setSpringContext(springContext)
                .addMigrationScanPackage("com.github.bovvver.migrations")
                .buildInitializingBeanRunner();
    }
}

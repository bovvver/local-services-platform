package com.github.bovvver.config;

import io.mongock.driver.mongodb.springdata.v4.SpringDataMongoV4Driver;
import io.mongock.runner.springboot.MongockSpringboot;
import io.mongock.runner.springboot.base.MongockInitializingBeanRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
class MongockConfig {

    @Bean
    public MongockInitializingBeanRunner mongockRunner(ApplicationContext springContext, MongoTemplate mongoTemplate) {
        return MongockSpringboot.builder()
                .setDriver(SpringDataMongoV4Driver.withDefaultLock(mongoTemplate))
                .setSpringContext(springContext)
                .addMigrationScanPackage("com.github.bovvver.migrations")
                .buildInitializingBeanRunner();
    }
}

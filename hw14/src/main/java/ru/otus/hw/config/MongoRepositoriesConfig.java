package ru.otus.hw.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = "ru.otus.hw.repositories.mongo"
)
public class MongoRepositoriesConfig {
}

package ru.otus.hw.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "ru.otus.hw.repositories.jpa"
)
public class JpaRepositoriesConfig {
}

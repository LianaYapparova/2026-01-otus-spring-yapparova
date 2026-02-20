package ru.otus.hw.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Bean
    AppProperties appProperties(@Value("${test.rightAnswersCountToPass}") int rightAnswersCountToPass,
                                @Value("${test.fileName}") String fileName) {
        AppProperties appProperties =  new AppProperties();
        appProperties.setRightAnswersCountToPass(rightAnswersCountToPass);
        appProperties.setTestFileName(fileName);
        return appProperties;
    }
}

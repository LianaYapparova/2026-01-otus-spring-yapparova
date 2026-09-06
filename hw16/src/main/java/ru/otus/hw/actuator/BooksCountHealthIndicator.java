package ru.otus.hw.actuator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import ru.otus.hw.repositories.JpaDataBookRepository;

@Component
@RequiredArgsConstructor
public class BooksCountHealthIndicator implements HealthIndicator {

    private final JpaDataBookRepository bookRepository;

    @Override
    public Health health() {
        try {
            return Health.up()
                    .withDetail("booksCount", bookRepository.count())
                    .build();
        } catch (Exception ex) {
            return Health.down(ex)
                    .withDetail("booksCount", "unavailable")
                    .build();
        }
    }
}

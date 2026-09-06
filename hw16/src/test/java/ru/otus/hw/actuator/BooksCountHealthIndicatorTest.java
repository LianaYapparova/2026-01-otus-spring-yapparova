package ru.otus.hw.actuator;

import org.junit.jupiter.api.Test;
import ru.otus.hw.repositories.JpaDataBookRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BooksCountHealthIndicatorTest {

    @Test
    void shouldReturnUpHealthWithBooksCountDetail() {
        var bookRepository = mock(JpaDataBookRepository.class);
        when(bookRepository.count()).thenReturn(7L);

        var indicator = new BooksCountHealthIndicator(bookRepository);

        var health = indicator.health();

        assertThat(health.getStatus().getCode()).isEqualTo("UP");
        assertThat(health.getDetails()).containsEntry("booksCount", 7L);
    }
}

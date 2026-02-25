package ru.otus.hw.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;
import ru.otus.hw.service.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CsvQuestionDaoTest {

    @Mock
    private TestFileNameProvider fileNameProvider;

    @Mock
    private ResourceLoader resourceLoader;

    @InjectMocks
    private CsvQuestionDao dao;

    @Test
    void shouldReadQuestionsCorrectlyTest() throws IOException {
        when(fileNameProvider.getTestFileName())
                .thenReturn("questions.csv");

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("questions.csv");

        when(resourceLoader.getResourceAsStream("questions.csv"))
                .thenReturn(inputStream);

        List<Question> questions = dao.findAll();

        assertThat(questions).isNotNull();
        assertThat(questions).hasSize(3);

        verify(fileNameProvider, times(1)).getTestFileName();
    }

    @Test
    void shouldThrowExceptionIfFileNotFoundTest() {
        when(fileNameProvider.getTestFileName()).thenReturn("not-existing-file.csv");

        assertThatThrownBy(dao::findAll)
                .isInstanceOf(QuestionReadException.class)
                .hasMessageContaining("The csv file could not be processed correctly");

        verify(fileNameProvider, times(2)).getTestFileName();
    }

    @Test
    void shouldThrowExceptionIfFileEmptyTest() throws IOException {
        when(fileNameProvider.getTestFileName())
                .thenReturn("empty-questions.csv");

        InputStream inputStream =
                getClass().getClassLoader()
                        .getResourceAsStream("empty-questions.csv");

        when(resourceLoader.getResourceAsStream("empty-questions.csv"))
                .thenReturn(inputStream);

        assertThatThrownBy(dao::findAll)
                .isInstanceOf(QuestionReadException.class)
                .hasMessageContaining("contains no questions");
    }

    @Test
    void shouldThrowQuestionReadExceptionWhenIOExceptionOccursTest() throws Exception {
        when(fileNameProvider.getTestFileName()).thenReturn("test.csv");

        when(resourceLoader.getResourceAsStream("test.csv"))
                .thenThrow(new IOException("IO error"));

        assertThatThrownBy(dao::findAll)
                .isInstanceOf(QuestionReadException.class)
                .hasMessageContaining("An error occurred when reading the csv file: test.csv")
                .hasCauseInstanceOf(IOException.class);
    }
}
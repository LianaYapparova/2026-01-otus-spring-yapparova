package ru.otus.hw.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;


@SpringBootTest(classes = CsvQuestionDao.class)
class CsvQuestionDaoTest {

    @MockBean
    private TestFileNameProvider fileNameProvider;

    @MockBean
    private ResourceLoader resourceLoader;

    @Autowired
    private CsvQuestionDao dao;

    @Test
    void shouldReadQuestionsCorrectlyTest() throws IOException {
        Resource resource = mock(Resource.class);
        when(fileNameProvider.getTestFileName())
                .thenReturn("questions.csv");

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("questions.csv");

        when(resourceLoader.getResource("questions.csv")).thenReturn(resource);
        when(resource.getInputStream()).thenReturn(inputStream);

        List<Question> questions = dao.findAll();

        assertThat(questions).isNotNull();
        assertThat(questions).hasSize(3);

        verify(fileNameProvider, times(1)).getTestFileName();
    }

    @Test
    void shouldThrowQuestionReadExceptionWhenIOExceptionOccursTest() {
        when(fileNameProvider.getTestFileName()).thenReturn("not-existing-file.csv");

        assertThatThrownBy(dao::findAll)
                .isInstanceOf(QuestionReadException.class)
                .hasMessageContaining("An error occurred when reading the csv file: ");

        verify(fileNameProvider, times(2)).getTestFileName();
    }

    @Test
    void shouldThrowExceptionIfFileEmptyTest() throws IOException {
        Resource resource = mock(Resource.class);

        InputStream inputStream = getClass().getClassLoader()
                        .getResourceAsStream("empty-questions.csv");

        when(fileNameProvider.getTestFileName()).thenReturn("empty-questions.csv");
        when(resourceLoader.getResource("empty-questions.csv")).thenReturn(resource);
        when(resource.getInputStream()).thenReturn(inputStream);


        assertThatThrownBy(dao::findAll)
                .isInstanceOf(QuestionReadException.class)
                .hasMessageContaining("Contains no questions");
    }

    @Test
    void shouldThrowQuestionReadExceptionWhenRuntimeExceptionOccursTest() throws Exception {
        Resource resource = mock(Resource.class);

        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("test.csv");

        when(fileNameProvider.getTestFileName()).thenReturn("test.csv");
        when(resourceLoader.getResource("test.csv")).thenReturn(null);
        when(resource.getInputStream()).thenReturn(inputStream);

        assertThatThrownBy(dao::findAll)
                .isInstanceOf(QuestionReadException.class)
                .hasMessageContaining("The csv file could not be processed correctly: test.csv")
                .hasNoCause();
    }
}
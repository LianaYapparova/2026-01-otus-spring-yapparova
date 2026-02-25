package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;

import java.util.List;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TestServiceImplTest {
    @Mock
    private IOService ioService;
    @Mock
    private CsvQuestionDao csvQuestionDao;

    @InjectMocks
    private TestServiceImpl testService;

    private List<Question> testQuestions;

    @BeforeEach
    void setUp() {
        testQuestions = List.of(
                new Question(
                        "Сколько будет 2+2?",
                        List.of(
                                new Answer("3", false),
                                new Answer("4", true)
                        )
                ),
                new Question(
                        "Столица России?",
                        List.of(
                                new Answer("Москва", true),
                                new Answer("Санкт-Петербург", false)
                        )
                )
        );
    }

    @DisplayName("TestService should get questions and print in certain order")
    @Test
    public void executeTestShouldGetQuestionsAndPrintInCertainOrder() {
        when(csvQuestionDao.findAll()).thenReturn(testQuestions);
        when(ioService.readIntForRange(anyInt(), anyInt(), anyString())).thenReturn(1);
        Student student = new Student("Liana", "Yapparova");
        testService.executeTestFor(student);

        InOrder inOrder = inOrder(csvQuestionDao, ioService);

        inOrder.verify(csvQuestionDao, times(1)).findAll();

        inOrder.verify(ioService).printLine(eq(""));
        inOrder.verify(ioService).printFormattedLine(eq("Please answer the questions below%n"));
        inOrder.verify(ioService).printLine(eq("Сколько будет 2+2?"));
        inOrder.verify(ioService).printLine(eq("Answer options:"));
        inOrder.verify(ioService).printFormattedLine(eq("Option %d: %s"), eq(1), eq("3"));
        inOrder.verify(ioService).printFormattedLine(eq("Option %d: %s"), eq(2), eq("4"));
        inOrder.verify(ioService, times(1)).readIntForRange(eq(1), eq(2), eq("The value entered is not included in the sample estimates."));

        inOrder.verify(ioService).printLine(eq("Столица России?"));
        inOrder.verify(ioService).printLine(eq("Answer options:"));
        inOrder.verify(ioService).printFormattedLine(eq("Option %d: %s"), eq(1), eq("Москва"));
        inOrder.verify(ioService).printFormattedLine(eq("Option %d: %s"), eq(2), eq("Санкт-Петербург"));
        inOrder.verify(ioService, times(1)).readIntForRange(eq(1), eq(2), eq("The value entered is not included in the sample estimates."));
        verifyNoMoreInteractions(ioService, csvQuestionDao);
    }

    @Test
    @DisplayName("Should handle empty question list")
    void shouldHandleEmptyQuestionList() {
        when(csvQuestionDao.findAll()).thenReturn(List.of());

        Student student = new Student("Liana", "Yapparova");
        testService.executeTestFor(student);


        verify(csvQuestionDao).findAll();
        verify(ioService).printLine("");
        verify(ioService).printFormattedLine(eq("Please answer the questions below%n"));
        verify(ioService, never()).printLine(contains("?"));
        verify(ioService, never()).printLine("Answer options:");
    }
}
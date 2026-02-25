package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        var questions = questionDao.findAll();
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        var testResult = new TestResult(student);

        for (var question : questions) {
            var isAnswerValid = false;
            printQuestion(question);

            int answer = ioService.readIntForRange(1, question.answers().size(),
                    "The value entered is not included in the sample estimates.");
            if (question.answers().get(answer - 1).isCorrect()) {
                isAnswerValid = true;
            }
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private void printQuestion(Question question) {
        ioService.printLine(question.text());
        ioService.printLine("Answer options:");
        IntStream.range(0, question.answers().size())
                .forEach(i -> ioService.printFormattedLine(
                        "Option %d: %s",
                        i + 1,
                        question.answers().get(i).text()
                ));
    }
}

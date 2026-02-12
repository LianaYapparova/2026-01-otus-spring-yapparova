package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Question;

import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final CsvQuestionDao csvQuestionDao;

    @Override
    public void executeTest() {
        List<Question> questions = csvQuestionDao.findAll();
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        questions.forEach(question -> {
            ioService.printLine(question.text());
            ioService.printLine("Answer options:");

            IntStream.range(0, question.answers().size())
                    .forEach(i -> ioService.printFormattedLine(
                            "Option %d: %s",
                            i + 1,
                            question.answers().get(i).text()
                    ));

            ioService.printLine("");
        });
    }
}

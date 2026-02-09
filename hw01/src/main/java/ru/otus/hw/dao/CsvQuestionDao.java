package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {

    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        List<QuestionDto> questions;

        try (InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream(fileNameProvider.getTestFileName());
             InputStreamReader streamReader = new InputStreamReader(Objects.requireNonNull(inputStream),
                     StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {
            questions = getQuestionDto(reader);
        } catch (IOException e) {
            throw new QuestionReadException("An error occurred when reading the csv file: "
                    + fileNameProvider.getTestFileName(), e);
        } catch (RuntimeException e) {
            throw new QuestionReadException("The csv file could not be processed correctly: "
                    + fileNameProvider.getTestFileName());
        }

        List<Question> questionList = questions.stream().map(QuestionDto::toDomainObject).toList();

        if (!questionList.isEmpty()) {
            return questionList;
        } else {
            throw new QuestionReadException(fileNameProvider.getTestFileName() + " contains no questions");
        }
    }

    private List<QuestionDto> getQuestionDto(BufferedReader reader) {
        List<QuestionDto> questions;
        CsvToBean<QuestionDto> csvToBean = new CsvToBeanBuilder<QuestionDto>(reader)
                .withType(QuestionDto.class)
                .withSeparator(';')
                .withIgnoreLeadingWhiteSpace(true)
                .withSkipLines(1)
                .build();

        questions = csvToBean.parse();
        return questions;
    }
}

package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.time.LocalDateTime;


@RequiredArgsConstructor
@ShellComponent
public class BatchCommands {
    private final JobLauncher jobLauncher;
    private final Job migrationJob;

    @ShellMethod(value = "Миграция PostgreSQL -> MongoDB", key = "migrate")
    public String migrate() throws Exception {
        JobParameters parameters =
                new JobParametersBuilder()
                        .addLocalDateTime("migrationTime", LocalDateTime.now())
                        .toJobParameters();

        JobExecution execution =
                jobLauncher.run(migrationJob, parameters);

        return "Migration started. Job execution id: " + execution.getId();
    }
}

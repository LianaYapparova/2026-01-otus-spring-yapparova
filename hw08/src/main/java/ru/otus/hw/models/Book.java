package ru.otus.hw.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"genres", "comments"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Document(collection = "books")
public class Book {
    @Id
    private String id;

    private String title;

    private Author author;

    private List<Genre> genres;
}

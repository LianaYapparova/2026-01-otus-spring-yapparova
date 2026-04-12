package ru.otus.hw.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.mongodb.client.MongoDatabase;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.List;

@ChangeLog
public class DatabaseChangelog {

    @ChangeSet(order = "001", id = "dropDb", author = "liana", runAlways = true)
    public void dropDb(MongoDatabase db) {
        db.drop();
    }

    @ChangeSet(order = "002", id = "insertAuthors", author = "liana")
    public void insertAuthors(MongockTemplate template) {
        template.insertAll(List.of(
                new Author().builder().id("author1").fullName("Mikhail Lermontov").build(),
                new Author().builder().id("author2").fullName("Leo Tolstoy").build(),
                new Author().builder().id("author3").fullName("Aleksandr Pushkin").build()
        ));
    }

    @ChangeSet(order = "003", id = "insertGenres", author = "liana")
    public void insertGenres(MongockTemplate template) {
        template.insertAll(List.of(
                new Genre().builder().id("genre1").name("Story").build(),
                new Genre().builder().id("genre2").name("Novel").build(),
                new Genre().builder().id("genre3").name("Poem").build()
        ));
    }

    @ChangeSet(order = "004", id = "insertBooks", author = "liana")
    public void insertBooks(MongockTemplate template) {
        template.insertAll(List.of(
                new Book().builder().id("book1").title("War and Peace").author(new Author().builder().id("author2")
                                .fullName("Leo Tolstoy").build())
                        .genres(List.of(new Genre().builder().id("genre2").name("Novel").build())).build(),
                new Book().builder().id("book2").title("Eugene Onegin").author(new Author().builder().id("author3")
                                .fullName("Aleksandr Pushkin").build())
                        .genres(List.of(new Genre().builder().id("genre3").name("Poem").build())).build(),
                new Book().builder().id("book3").title("Hero of our time").author(new Author().builder().id("author1")
                                .fullName("Mikhail Lermontov").build())
                        .genres(List.of(new Genre().builder().id("genre1").name("Story").build())).build()
        ));
    }

    @ChangeSet(order = "005", id = "insertComments", author = "liana")
    public void insertComments(MongockTemplate template) {
        template.insertAll(List.of(
                new Comment().builder().id("comment1").text("Good book and very long").bookId("book1").build(),
                new Comment().builder().id("comment2").text("I want to cry").bookId("book2").build(),
                new Comment().builder().id("comment3").text("I dont read, but think its very interested")
                        .bookId("book3").build()
        ));
    }
}

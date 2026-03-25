package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

@Repository
public class JpaBookRepository implements BookRepository {

    @PersistenceContext
    private final EntityManager em;

    public JpaBookRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Optional<Book> findById(long id) {
        EntityGraph<?> graph = em.getEntityGraph("book-author-entity-graph");
        List<Book> result = em.createQuery("select b from Book b left join fetch b.genres where b.id = :id ", Book.class)
                .setParameter("id", id)
                .setHint(FETCH.getKey(), graph)
                .getResultList();
        return result.stream().findFirst();
    }

    @Override
    public List<Book> findAll() {
        EntityGraph<?> graph = em.getEntityGraph("book-author-entity-graph");
        List<Book> bookList = em.createQuery("select b from Book b left join fetch b.genres", Book.class)
                .setHint(FETCH.getKey(), graph)
                .getResultList();
        return bookList;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            em.persist(book);
            return book;
        }
        if (Objects.isNull(em.find(Book.class, book.getId()))) {
            throw new EntityNotFoundException("Book with id " + book.getId() + " not found");
        }
        return em.merge(book);
    }

    @Override
    public void deleteById(long id) {
        em.createQuery("delete from Book b where b.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }
}

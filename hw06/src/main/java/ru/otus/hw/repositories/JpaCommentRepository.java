package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

@Repository
public class JpaCommentRepository implements CommentRepository {

    @PersistenceContext
    private final EntityManager em;

    public JpaCommentRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Optional<Comment> findById(long id) {
        EntityGraph<?> graph = em.getEntityGraph("book-entity-graph");
        List<Comment> result = em.createQuery("select c from Comment c where c.id = :id ", Comment.class)
                .setParameter("id", id)
                .setHint(FETCH.getKey(), graph)
                .getResultList();
        return result.stream().findFirst();
    }

    @Override
    public List<Comment> findByBookId(long id) {
        TypedQuery<Comment> query = em.createQuery("select c " +
                        "from Comment c " +
                        "where c.book.id = :id",
                Comment.class);
        query.setParameter("id", id);
        return query.getResultList();
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == 0) {
            em.persist(comment);
            return comment;
        }
        return em.merge(comment);
    }

    @Override
    public void deleteById(long id) {
        em.createQuery("delete from Comment c where c.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }
}

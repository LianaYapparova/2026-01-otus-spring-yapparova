package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.JpaDataBookRepository;
import ru.otus.hw.repositories.JpaDataCommentRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final JpaDataCommentRepository commentRepository;

    private final JpaDataBookRepository bookRepository;

    @Transactional(readOnly = true)
    @Override
    public Optional<Comment> findById(long id) {
        return commentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Comment> findByBookId(long bookId) {
        return commentRepository.findByBookId(bookId);
    }

    @Transactional
    @Override
    public Comment insert(String text, long bookId) {
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(bookId)));

        return commentRepository.save(new Comment(0, text, book));
    }

    @Transactional
    @Override
    public Comment update(long id, String text, long bookId) {
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(bookId)));
        return commentRepository.save(new Comment(id, text, book));
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }
}

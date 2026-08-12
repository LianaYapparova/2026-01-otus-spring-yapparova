package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.JpaDataAuthorRepository;
import ru.otus.hw.repositories.JpaDataBookRepository;
import ru.otus.hw.repositories.JpaDataGenreRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final JpaDataAuthorRepository authorRepository;

    private final JpaDataGenreRepository genreRepository;

    private final JpaDataBookRepository bookRepository;
    private final AclServiceWrapperService aclServiceWrapperService;

    @Transactional(readOnly = true)
    @Override
    @PreAuthorize("hasPermission(#id, 'ru.otus.hw.models.Book', 'READ')")
    public Optional<Book> findById(long id) {
        return bookRepository.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    @PostFilter("hasPermission(filterObject, 'READ')")
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Transactional
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Book insert(String title, long authorId, Set<Long> genresIds) {
        return save(0, title, authorId, genresIds);
    }

    @Transactional
    @Override
    @PreAuthorize("hasPermission(#id, 'ru.otus.hw.models.Book', 'WRITE')")
    public Book update(long id, String title, long authorId, Set<Long> genresIds) {
        return save(id, title, authorId, genresIds);
    }

    @Transactional
    @Override
    @PreAuthorize("hasPermission(#id, 'ru.otus.hw.models.Book', 'DELETE')")
    public void deleteById(long id) {
        bookRepository.deleteById(id);
    }

    private Book save(long id, String title, long authorId, Set<Long> genresIds) {
        var isNewBook = id == 0;

        if (isEmpty(genresIds)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }

        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));
        var genres = genreRepository.findAllById(genresIds);
        if (isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
        }

        var book = new Book(id, title, author, genres);
        book = bookRepository.save(book);
        if (isNewBook) {
            aclServiceWrapperService.createPermission(book, BasePermission.READ);
        }
        return book;
    }
}

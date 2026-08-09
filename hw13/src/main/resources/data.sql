insert into authors(full_name)
values ('Author_1'),
       ('Author_2'),
       ('Author_3');

insert into genres(name)
values ('Genre_1'),
       ('Genre_2'),
       ('Genre_3'),
       ('Genre_4'),
       ('Genre_5'),
       ('Genre_6');

insert into books(title, author_id)
values ('BookTitle_1', 1),
       ('BookTitle_2', 2),
       ('BookTitle_3', 3);

insert into books_genres(book_id, genre_id)
values (1, 1),
       (1, 2),
       (2, 3),
       (2, 4),
       (3, 5),
       (3, 6);

insert into comments(text, book_id)
values ('comment1', 1),
       ('comment2', 1),
       ('comment3', 2),
       ('comment4', 2);

insert into users(username, password, role)
values ('petrov', '$2a$10$CezVUiWcevWJytzRp/4VJu8AGLgWLaG1dVinP8RaM6JRqE7AT/ruy', 'USER'),
       ('ivanov', '$2a$10$WDdFiVm93UIcsYmPMWLUpeD6cuk4K7prcfBFXkZMWXNchbJ1VYwLe', 'USER'),
       ('administrator', '$2a$10$.AHVqQbXjLJU1b7RFvVfMOyiA7cgi/SUs/DMVud6HYsQBu6VtY2.2', 'ADMIN');


INSERT INTO acl_class (id, class)
VALUES (1, 'ru.otus.hw.models.Book');


INSERT INTO acl_sid (id, principal, sid)
VALUES
    (1, TRUE, 'petrov'),
    (2, TRUE, 'ivanov'),
    (3, FALSE, 'ROLE_ADMIN'),
    (4, TRUE, 'administrator');


INSERT INTO acl_object_identity (
    id,
    object_id_class,
    object_id_identity,
    parent_object,
    owner_sid,
    entries_inheriting
)
VALUES
    (1, 1, 1, NULL, 3, FALSE),
    (2, 1, 2, NULL, 3, FALSE),
    (3, 1, 3, NULL, 3, FALSE);


INSERT INTO acl_entry (
    id,
    acl_object_identity,
    ace_order,
    sid,
    mask,
    granting,
    audit_success,
    audit_failure
)
VALUES
    (1, 1, 1, 1, 1, TRUE, TRUE, TRUE),
    (2, 1, 2, 3, 16, TRUE, TRUE, TRUE),
    (7, 1, 3, 3, 1, TRUE, TRUE, TRUE),
    (10, 1, 4, 3, 2, TRUE, TRUE, TRUE),
    (13, 1, 5, 3, 8, TRUE, TRUE, TRUE),

    (3, 2, 1, 1, 1, TRUE, TRUE, TRUE),
    (4, 2, 2, 3, 16, TRUE, TRUE, TRUE),
    (8, 2, 3, 3, 1, TRUE, TRUE, TRUE),
    (11, 2, 4, 3, 2, TRUE, TRUE, TRUE),
    (14, 2, 5, 3, 8, TRUE, TRUE, TRUE),

    (5, 3, 1, 2, 1, TRUE, TRUE, TRUE),
    (6, 3, 2, 3, 16, TRUE, TRUE, TRUE),
    (9, 3, 3, 3, 1, TRUE, TRUE, TRUE),
    (12, 3, 4, 3, 2, TRUE, TRUE, TRUE),
    (15, 3, 5, 3, 8, TRUE, TRUE, TRUE);

ALTER TABLE acl_class ALTER COLUMN id RESTART WITH 2;
ALTER TABLE acl_sid ALTER COLUMN id RESTART WITH 5;
ALTER TABLE acl_object_identity ALTER COLUMN id RESTART WITH 4;
ALTER TABLE acl_entry ALTER COLUMN id RESTART WITH 13;

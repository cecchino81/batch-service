package it.batch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.batch.entity.BookEntity;

public interface BookRepository extends JpaRepository<BookEntity, Long> {
}

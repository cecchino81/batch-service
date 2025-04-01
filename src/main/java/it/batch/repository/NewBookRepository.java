package it.batch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.batch.entity.NewBookEntity;

public interface NewBookRepository extends JpaRepository<NewBookEntity, Long> {
}

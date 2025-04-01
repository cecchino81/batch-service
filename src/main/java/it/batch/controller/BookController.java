package it.batch.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.batch.entity.BookEntity;
import it.batch.repository.BookRepository;

import java.util.List;

@RestController
@RequestMapping("/book")
@Slf4j
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @GetMapping
    public List<BookEntity> getAll() {
        log.info("Handling find all");
        log.info("Hi there");
        return bookRepository.findAll();
    }
}

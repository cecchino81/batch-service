package it.batch.copydb;

import java.util.Iterator;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.batch.entity.BookEntity;
import it.batch.repository.BookRepository;

@Component
public class BookDbReader implements ItemReader<BookEntity> {

    @Autowired
    private BookRepository bookRepository;

    private Iterator<BookEntity> bookIterator;

    @Override
    public BookEntity read() throws Exception {
        if (bookIterator == null || !bookIterator.hasNext()) {
            List<BookEntity> books = bookRepository.findAll();
            bookIterator = books.iterator();
        }
        return bookIterator.hasNext() ? bookIterator.next() : null;
    }
}
package it.batch.copydb;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import it.batch.entity.BookEntity;
import it.batch.entity.NewBookEntity;

@Component
public class BookDbProcessor implements ItemProcessor<BookEntity, NewBookEntity> {

    @Override
    public NewBookEntity process(BookEntity book) throws Exception {
        return new NewBookEntity(book.getTitle(), book.getAuthor(), book.getYearOfPublishing());
    }
}
package it.batch.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import it.batch.entity.BookEntity;

@Slf4j
public class BookAuthorProcessor implements ItemProcessor<BookEntity, BookEntity> {
    @Override
    public BookEntity process(BookEntity item) throws Exception {
        log.info("Process author: {}", item);
        item.setAuthor("By " + item.getAuthor());
        return item;
    }
}

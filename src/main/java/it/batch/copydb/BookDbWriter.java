package it.batch.copydb;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.batch.entity.NewBookEntity;
import it.batch.repository.NewBookRepository;

@Component
public class BookDbWriter implements ItemWriter<NewBookEntity> {

    @Autowired
    private NewBookRepository newBookRepository;

    @Override
    public void write(Chunk<? extends NewBookEntity> chunk) throws Exception {
        newBookRepository.saveAll(chunk.getItems());
    }
}
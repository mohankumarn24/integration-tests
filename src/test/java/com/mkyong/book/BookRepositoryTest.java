package com.mkyong.book;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.Optional;

// This example shows how to test the repository using only the @DataJpaTest and Testcontainers
@DataJpaTest
// do not replace the testcontainer data source
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Test
    public void testBookSaveAndFindById() {

        // Create a new book
        Book book = new Book();
        book.setName("Is Java Dead?");
        book.setIsbn("111-111");

        // save book
        bookRepository.save(book);

        // find book
        Optional<Book> result = bookRepository.findById(book.getId());
        Assertions.assertTrue(result.isPresent());
        Book bookFromGet = result.get();
        Assertions.assertEquals("Is Java Dead?", bookFromGet.getName());
        Assertions.assertEquals("111-111", bookFromGet.getIsbn());
    }

    @Test
    public void testBookCRUD() {

        Book book = new Book();
        book.setName("Is Java Dead?");
        book.setIsbn("111-111");

        // save book
        bookRepository.save(book);

        // find book by isbn
        Optional<Book> result = bookRepository.findByIsbn(book.getIsbn());
        Assertions.assertTrue(result.isPresent());
        Book bookFromGet = result.get();
        Long bookId = bookFromGet.getId();
        Assertions.assertEquals("Is Java Dead?", bookFromGet.getName());
        Assertions.assertEquals("111-111", bookFromGet.getIsbn());

        // update book
        book.setName("Java still relevant in 2050");
        bookRepository.save(book);

        // find book by id
        Optional<Book> result2 = bookRepository.findById(bookId);
        Assertions.assertTrue(result2.isPresent());
        Book bookFromGet2 = result2.get();
        Assertions.assertEquals("Java still relevant in 2050", bookFromGet2.getName());
        Assertions.assertEquals("111-111", bookFromGet2.getIsbn());

        // delete a book
        bookRepository.delete(book);
        // should be empty
        Assertions.assertTrue(bookRepository.findById(bookId).isEmpty());
    }
}
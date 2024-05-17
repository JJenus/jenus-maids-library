package com.maids.LMS.book;

import com.maids.LMS.borrowing.BorrowingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Service
@CacheConfig(cacheNames = "books")
public class BookService {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BorrowingRepository borrowingRepository;

    @Cacheable
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Cacheable
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Book not found"));
    }

    @Transactional
    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    @Transactional
    public Book updateBook(Long id, Book updatedBook) {
        if (bookRepository.existsById(id)) {
            updatedBook.setId(id);
            return bookRepository.save(updatedBook);
        }
        throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Book not found");
    }

    @Transactional
    public void deleteBook(Long id) {
        bookRepository.findById(id).ifPresent(book -> borrowingRepository.deleteByBook(book));
        bookRepository.deleteById(id);
    }
}

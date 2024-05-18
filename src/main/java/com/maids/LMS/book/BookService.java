package com.maids.LMS.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
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

    @Cacheable(key = "'allBooks'")
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Cacheable(key = "#id")
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Book not found"));
    }

    @Transactional
    @CachePut(key = "#result.id")
    @CacheEvict(key = "'allBooks'")  // Clears all cache entries for books to reflect changes
    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    @Transactional
    @CachePut(key = "#id")
    @CacheEvict(key = "'allBooks'")
    public Book updateBook(Long id, Book updatedBook) {
        if (bookRepository.existsById(id)) {
            updatedBook.setId(id);
            return bookRepository.save(updatedBook);
        }
        throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Book not found");
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(key = "#id"),
            @CacheEvict(key = "'allBooks'")
    })
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}

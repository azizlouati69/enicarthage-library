package com.enicarthage.library.service;

import com.enicarthage.library.entity.Book;
import com.enicarthage.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    public Book createBook(Book book) {
        if (book.getIsbn() != null && !book.getIsbn().isEmpty()) {
            List<Book> existingBooks = bookRepository.findByIsbn(book.getIsbn());
            if (!existingBooks.isEmpty()) {
                throw new RuntimeException("Book with ISBN already exists");
            }
        }
        
        book.setCreatedAt(LocalDateTime.now());
        book.setUpdatedAt(LocalDateTime.now());
        book.setStatus(Book.BookStatus.AVAILABLE);
        
        return bookRepository.save(book);
    }
    
    public Book updateBook(Long id, Book bookDetails) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        if (bookDetails.getIsbn() != null && !bookDetails.getIsbn().isEmpty() && 
            !bookDetails.getIsbn().equals(book.getIsbn())) {
            List<Book> existingBooks = bookRepository.findByIsbn(bookDetails.getIsbn());
            if (!existingBooks.isEmpty()) {
                throw new RuntimeException("Book with ISBN already exists");
            }
        }
        
        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setIsbn(bookDetails.getIsbn());
        book.setPublisher(bookDetails.getPublisher());
        book.setPublicationYear(bookDetails.getPublicationYear());
        book.setCategory(bookDetails.getCategory());
        book.setDescription(bookDetails.getDescription());
        book.setCoverImageUrl(bookDetails.getCoverImageUrl());
        book.setTotalCopies(bookDetails.getTotalCopies());
        book.setAvailableCopies(bookDetails.getAvailableCopies());
        book.setShelfLocation(bookDetails.getShelfLocation());
        book.setLanguage(bookDetails.getLanguage());
        book.setPages(bookDetails.getPages());
        book.setPrice(bookDetails.getPrice());
        book.setUpdatedAt(LocalDateTime.now());
        
        return bookRepository.save(book);
    }
    
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        bookRepository.delete(book);
    }
    
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }
    
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }
    
    public List<Book> searchBooks(String searchTerm) {
        return bookRepository.findBySearchTerm(searchTerm);
    }
    
    public Page<Book> searchBooks(String searchTerm, Pageable pageable) {
        return bookRepository.findBySearchTerm(searchTerm, pageable);
    }
    
    public List<Book> getBooksByCategory(Book.BookCategory category) {
        return bookRepository.findByCategory(category);
    }
    
    public List<Book> getAvailableBooks() {
        return bookRepository.findAvailableBooks();
    }
    
    public List<Book> getBooksByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }
    
    public List<Book> getBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }
    
    public List<Book> getBooksByPublicationYear(Integer year) {
        return bookRepository.findByPublicationYear(year);
    }
    
    public List<Book> getBooksByLanguage(String language) {
        return bookRepository.findByLanguage(language);
    }
    
    public Book updateBookStatus(Long id, Book.BookStatus status) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        book.setStatus(status);
        book.setUpdatedAt(LocalDateTime.now());
        return bookRepository.save(book);
    }
    
    public Book updateAvailableCopies(Long id, Integer availableCopies) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        book.setAvailableCopies(availableCopies);
        book.setUpdatedAt(LocalDateTime.now());
        return bookRepository.save(book);
    }
}
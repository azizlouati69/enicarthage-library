package com.enicarthage.library.repository;

import com.enicarthage.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    List<Book> findByTitleContainingIgnoreCase(String title);
    
    List<Book> findByAuthorContainingIgnoreCase(String author);
    
    List<Book> findByIsbn(String isbn);
    
    List<Book> findByCategory(Book.BookCategory category);
    
    List<Book> findByStatus(Book.BookStatus status);
    
    List<Book> findByPublisherContainingIgnoreCase(String publisher);
    
    @Query("SELECT b FROM Book b WHERE b.title LIKE %:searchTerm% OR b.author LIKE %:searchTerm% OR b.isbn LIKE %:searchTerm%")
    List<Book> findBySearchTerm(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0")
    List<Book> findAvailableBooks();
    
    @Query("SELECT b FROM Book b WHERE b.availableCopies = 0")
    List<Book> findUnavailableBooks();
    
    @Query("SELECT b FROM Book b WHERE b.publicationYear = :year")
    List<Book> findByPublicationYear(@Param("year") Integer year);
    
    @Query("SELECT b FROM Book b WHERE b.publicationYear BETWEEN :startYear AND :endYear")
    List<Book> findByPublicationYearRange(@Param("startYear") Integer startYear, @Param("endYear") Integer endYear);
    
    @Query("SELECT b FROM Book b WHERE b.language = :language")
    List<Book> findByLanguage(@Param("language") String language);
    
    @Query("SELECT b FROM Book b WHERE b.category = :category AND b.availableCopies > 0")
    List<Book> findAvailableBooksByCategory(@Param("category") Book.BookCategory category);
    
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    Page<Book> findByAuthorContainingIgnoreCase(String author, Pageable pageable);
    
    Page<Book> findByCategory(Book.BookCategory category, Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE b.title LIKE %:searchTerm% OR b.author LIKE %:searchTerm% OR b.isbn LIKE %:searchTerm%")
    Page<Book> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);
}
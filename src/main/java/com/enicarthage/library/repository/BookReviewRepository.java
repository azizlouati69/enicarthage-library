package com.enicarthage.library.repository;

import com.enicarthage.library.entity.Book;
import com.enicarthage.library.entity.BookReview;
import com.enicarthage.library.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookReviewRepository extends JpaRepository<BookReview, Long> {
    
    List<BookReview> findByBook(Book book);
    
    Page<BookReview> findByBook(Book book, Pageable pageable);
    
    List<BookReview> findByUser(User user);
    
    List<BookReview> findByUserAndBook(User user, Book book);
    
    List<BookReview> findByRating(Integer rating);
    
    List<BookReview> findByBookAndRating(Book book, Integer rating);
    
    List<BookReview> findByIsVerified(Boolean isVerified);
    
    List<BookReview> findByBookAndIsVerified(Book book, Boolean isVerified);
    
    Long countByBook(Book book);
    
    Long countByUser(User user);
    
    Long countByRating(Integer rating);
    
    Long countByIsVerified(Boolean isVerified);
    
    @Query("SELECT AVG(r.rating) FROM BookReview r WHERE r.book = :book")
    Double getAverageRatingByBook(@Param("book") Book book);
    
    @Query("SELECT r FROM BookReview r WHERE r.book = :book AND r.rating = :rating")
    List<BookReview> findByBookAndRating(@Param("book") Book book, @Param("rating") Integer rating);
    
    @Query("SELECT r FROM BookReview r WHERE r.createdAt BETWEEN :startDate AND :endDate")
    List<BookReview> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT r FROM BookReview r WHERE r.book = :book ORDER BY r.createdAt DESC")
    List<BookReview> findByBookOrderByCreatedAtDesc(@Param("book") Book book);
    
    @Query("SELECT r FROM BookReview r WHERE r.rating >= :minRating AND r.rating <= :maxRating")
    List<BookReview> findByRatingRange(@Param("minRating") Integer minRating, @Param("maxRating") Integer maxRating);
    
    @Query("SELECT r FROM BookReview r WHERE r.review LIKE %:searchTerm%")
    List<BookReview> findByReviewContaining(@Param("searchTerm") String searchTerm);
}

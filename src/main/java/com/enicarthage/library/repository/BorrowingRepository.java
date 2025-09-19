package com.enicarthage.library.repository;

import com.enicarthage.library.entity.Borrowing;
import com.enicarthage.library.entity.User;
import com.enicarthage.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {
    
    List<Borrowing> findByUser(User user);
    
    List<Borrowing> findByBook(Book book);
    
    List<Borrowing> findByStatus(Borrowing.BorrowingStatus status);
    
    List<Borrowing> findByUserAndStatus(User user, Borrowing.BorrowingStatus status);
    
    @Query("SELECT b FROM Borrowing b WHERE b.user = :user AND b.status = 'ACTIVE'")
    List<Borrowing> findActiveBorrowingsByUser(@Param("user") User user);
    
    @Query("SELECT b FROM Borrowing b WHERE b.dueDate < :currentDate AND b.status = 'ACTIVE'")
    List<Borrowing> findOverdueBorrowings(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT b FROM Borrowing b WHERE b.user = :user AND b.dueDate < :currentDate AND b.status = 'ACTIVE'")
    List<Borrowing> findOverdueBorrowingsByUser(@Param("user") User user, @Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT b FROM Borrowing b WHERE b.borrowDate BETWEEN :startDate AND :endDate")
    List<Borrowing> findBorrowingsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT b FROM Borrowing b WHERE b.returnDate BETWEEN :startDate AND :endDate")
    List<Borrowing> findReturnsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(b) FROM Borrowing b WHERE b.user = :user AND b.status = 'ACTIVE'")
    Long countActiveBorrowingsByUser(@Param("user") User user);
    
    @Query("SELECT b FROM Borrowing b WHERE b.book = :book AND b.status = 'ACTIVE'")
    List<Borrowing> findActiveBorrowingsByBook(@Param("book") Book book);
    
    List<Borrowing> findByUserAndBookAndStatus(User user, Book book, Borrowing.BorrowingStatus status);
    
    Long countByStatus(Borrowing.BorrowingStatus status);
    
    @Query("SELECT COUNT(b) FROM Borrowing b WHERE b.dueDate < :currentDate AND b.status = 'ACTIVE'")
    Long countOverdueBorrowings(@Param("currentDate") LocalDateTime currentDate);
}
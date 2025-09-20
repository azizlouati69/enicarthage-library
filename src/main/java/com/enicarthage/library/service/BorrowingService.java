package com.enicarthage.library.service;

import com.enicarthage.library.entity.Book;
import com.enicarthage.library.entity.Borrowing;
import com.enicarthage.library.entity.User;
import com.enicarthage.library.repository.BookRepository;
import com.enicarthage.library.repository.BorrowingRepository;
import com.enicarthage.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class BorrowingService {
    
    @Autowired
    private BorrowingRepository borrowingRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private static final int STUDENT_BORROW_DAYS = 14;
    private static final int FACULTY_BORROW_DAYS = 30;
    private static final double DAILY_FINE_RATE = 1.0;
    
    public Borrowing borrowBook(Long bookId, Long userId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if book is available
        if (book.getAvailableCopies() <= 0) {
            throw new RuntimeException("Book is not available for borrowing");
        }
        
        // Check if user has active borrowings for this book
        List<Borrowing> activeBorrowings = borrowingRepository.findByUserAndBookAndStatus(
                user, book, Borrowing.BorrowingStatus.ACTIVE);
        if (!activeBorrowings.isEmpty()) {
            throw new RuntimeException("User has already borrowed this book");
        }
        
        // Check user's borrowing limit based on role
        List<Borrowing> userActiveBorrowings = borrowingRepository.findByUserAndStatus(
                user, Borrowing.BorrowingStatus.ACTIVE);
        
        int maxBorrowings = switch (user.getRole()) {
            case STUDENT -> 5;
            case FACULTY -> 10;
            case LIBRARIAN -> 15;
            default -> 3;
        };
        
        if (userActiveBorrowings.size() >= maxBorrowings) {
            throw new RuntimeException("User has reached maximum borrowing limit");
        }
        
        // Create borrowing record
        Borrowing borrowing = new Borrowing();
        borrowing.setUser(user);
        borrowing.setBook(book);
        borrowing.setBorrowDate(LocalDateTime.now());
        
        // Set due date based on user role
        int borrowDays = switch (user.getRole()) {
            case STUDENT -> STUDENT_BORROW_DAYS;
            case FACULTY -> FACULTY_BORROW_DAYS;
            default -> STUDENT_BORROW_DAYS;
        };
        borrowing.setDueDate(LocalDateTime.now().plusDays(borrowDays));
        borrowing.setStatus(Borrowing.BorrowingStatus.ACTIVE);
        
        // Update book available copies
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
        
        return borrowingRepository.save(borrowing);
    }
    
    public Borrowing returnBook(Long borrowingId) {
        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new RuntimeException("Borrowing record not found"));
        
        if (borrowing.getStatus() != Borrowing.BorrowingStatus.ACTIVE) {
            throw new RuntimeException("Book is not currently borrowed");
        }
        
        // Update borrowing record
        borrowing.setReturnDate(LocalDateTime.now());
        borrowing.setStatus(Borrowing.BorrowingStatus.RETURNED);
        
        // Calculate fine if overdue
        if (borrowing.getReturnDate().isAfter(borrowing.getDueDate())) {
            long overdueDays = ChronoUnit.DAYS.between(borrowing.getDueDate(), borrowing.getReturnDate());
            double fineAmount = overdueDays * DAILY_FINE_RATE;
            borrowing.setFineAmount(fineAmount);
        }
        
        // Update book available copies
        Book book = borrowing.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
        
        return borrowingRepository.save(borrowing);
    }
    
    public List<Borrowing> getAllBorrowings() {
        return borrowingRepository.findAll();
    }
    
    public Page<Borrowing> getAllBorrowings(Pageable pageable) {
        return borrowingRepository.findAll(pageable);
    }
    
    public List<Borrowing> getBorrowingsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return borrowingRepository.findByUser(user);
    }
    
    public List<Borrowing> getBorrowingsByUser(User user) {
        return borrowingRepository.findByUser(user);
    }
    
    public List<Borrowing> getBorrowingsByBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        return borrowingRepository.findByBook(book);
    }
    
    public List<Borrowing> getActiveBorrowings() {
        return borrowingRepository.findByStatus(Borrowing.BorrowingStatus.ACTIVE);
    }
    
    public List<Borrowing> getOverdueBorrowings() {
        return borrowingRepository.findOverdueBorrowings(LocalDateTime.now());
    }
    
    public Borrowing extendDueDate(Long borrowingId, int days) {
        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new RuntimeException("Borrowing record not found"));
        
        if (borrowing.getStatus() != Borrowing.BorrowingStatus.ACTIVE) {
            throw new RuntimeException("Cannot extend due date for inactive borrowing");
        }
        
        // Check if already extended (limit to 2 extensions)
        if (borrowing.getDueDate().isAfter(LocalDateTime.now().plusDays(7))) {
            throw new RuntimeException("Due date has already been extended");
        }
        
        borrowing.setDueDate(borrowing.getDueDate().plusDays(days));
        return borrowingRepository.save(borrowing);
    }
    
    public Borrowing updateFine(Long borrowingId, Double fineAmount) {
        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new RuntimeException("Borrowing record not found"));
        
        borrowing.setFineAmount(fineAmount);
        return borrowingRepository.save(borrowing);
    }
    
    public Map<String, Object> getBorrowingStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        statistics.put("totalBorrowings", borrowingRepository.count());
        statistics.put("activeBorrowings", borrowingRepository.countByStatus(Borrowing.BorrowingStatus.ACTIVE));
        statistics.put("overdueBorrowings", borrowingRepository.countOverdueBorrowings(LocalDateTime.now()));
        statistics.put("returnedBorrowings", borrowingRepository.countByStatus(Borrowing.BorrowingStatus.RETURNED));
        
        // Calculate total fines
        Double totalFines = borrowingRepository.findAll().stream()
                .mapToDouble(borrowing -> borrowing.getFineAmount() != null ? borrowing.getFineAmount() : 0.0)
                .sum();
        statistics.put("totalFines", totalFines);
        
        return statistics;
    }
    
    public Optional<Borrowing> getBorrowingById(Long id) {
        return borrowingRepository.findById(id);
    }
    
    public void deleteBorrowing(Long id) {
        Borrowing borrowing = borrowingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Borrowing record not found"));
        borrowingRepository.delete(borrowing);
    }
}

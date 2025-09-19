package com.enicarthage.library.controller;

import com.enicarthage.library.entity.Borrowing;
import com.enicarthage.library.service.BorrowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/borrowings")
@CrossOrigin(origins = "*")
public class BorrowingController {
    
    @Autowired
    private BorrowingService borrowingService;
    
    @PostMapping("/borrow")
    @PreAuthorize("hasRole('STUDENT') or hasRole('FACULTY')")
    public ResponseEntity<?> borrowBook(@RequestParam Long bookId, @RequestParam Long userId) {
        try {
            Borrowing borrowing = borrowingService.borrowBook(bookId, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Book borrowed successfully");
            response.put("borrowing", borrowing);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/return")
    @PreAuthorize("hasRole('STUDENT') or hasRole('FACULTY') or hasRole('LIBRARIAN')")
    public ResponseEntity<?> returnBook(@RequestParam Long borrowingId) {
        try {
            Borrowing borrowing = borrowingService.returnBook(borrowingId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Book returned successfully");
            response.put("borrowing", borrowing);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ResponseEntity<Page<Borrowing>> getAllBorrowings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "borrowDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Borrowing> borrowings = borrowingService.getAllBorrowings(pageable);
        return ResponseEntity.ok(borrowings);
    }
    
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ResponseEntity<List<Borrowing>> getBorrowingsByUser(@PathVariable Long userId) {
        List<Borrowing> borrowings = borrowingService.getBorrowingsByUser(userId);
        return ResponseEntity.ok(borrowings);
    }
    
    @GetMapping("/my-borrowings")
    @PreAuthorize("hasRole('STUDENT') or hasRole('FACULTY')")
    public ResponseEntity<List<Borrowing>> getMyBorrowings() {
        // Get current user ID from security context
        // This would need to be implemented based on your security setup
        List<Borrowing> borrowings = borrowingService.getBorrowingsByUser(1L); // Placeholder
        return ResponseEntity.ok(borrowings);
    }
    
    @GetMapping("/overdue")
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ResponseEntity<List<Borrowing>> getOverdueBorrowings() {
        List<Borrowing> overdueBorrowings = borrowingService.getOverdueBorrowings();
        return ResponseEntity.ok(overdueBorrowings);
    }
    
    @GetMapping("/active")
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ResponseEntity<List<Borrowing>> getActiveBorrowings() {
        List<Borrowing> activeBorrowings = borrowingService.getActiveBorrowings();
        return ResponseEntity.ok(activeBorrowings);
    }
    
    @GetMapping("/book/{bookId}")
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ResponseEntity<List<Borrowing>> getBorrowingsByBook(@PathVariable Long bookId) {
        List<Borrowing> borrowings = borrowingService.getBorrowingsByBook(bookId);
        return ResponseEntity.ok(borrowings);
    }
    
    @PatchMapping("/{id}/extend")
    @PreAuthorize("hasRole('STUDENT') or hasRole('FACULTY') or hasRole('LIBRARIAN')")
    public ResponseEntity<?> extendDueDate(@PathVariable Long id, @RequestParam int days) {
        try {
            Borrowing borrowing = borrowingService.extendDueDate(id, days);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Due date extended successfully");
            response.put("borrowing", borrowing);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PatchMapping("/{id}/fine")
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ResponseEntity<?> updateFine(@PathVariable Long id, @RequestParam Double fineAmount) {
        try {
            Borrowing borrowing = borrowingService.updateFine(id, fineAmount);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Fine updated successfully");
            response.put("borrowing", borrowing);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getBorrowingStatistics() {
        Map<String, Object> statistics = borrowingService.getBorrowingStatistics();
        return ResponseEntity.ok(statistics);
    }
}

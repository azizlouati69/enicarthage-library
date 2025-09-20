package com.enicarthage.library.controller;

import com.enicarthage.library.service.BookService;
import com.enicarthage.library.service.BorrowingService;
import com.enicarthage.library.service.EventService;
import com.enicarthage.library.service.UserService;
import com.enicarthage.library.service.BookReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private BorrowingService borrowingService;
    
    @Autowired
    private EventService eventService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private BookReviewService bookReviewService;
    
    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<Map<String, Object>> getDashboardOverview() {
        Map<String, Object> overview = new HashMap<>();
        
        // Book statistics
        Map<String, Object> bookStats = new HashMap<>();
        bookStats.put("totalBooks", bookService.getAllBooks().size());
        bookStats.put("availableBooks", bookService.getAvailableBooks().size());
        
        // Borrowing statistics
        Map<String, Object> borrowingStats = borrowingService.getBorrowingStatistics();
        
        // User statistics
        Map<String, Object> userStats = new HashMap<>();
        userStats.put("totalUsers", userService.getAllUsers().size());
        userStats.put("students", userService.getUsersByRole(com.enicarthage.library.entity.User.Role.STUDENT).size());
        userStats.put("faculty", userService.getUsersByRole(com.enicarthage.library.entity.User.Role.FACULTY).size());
        userStats.put("librarians", userService.getUsersByRole(com.enicarthage.library.entity.User.Role.LIBRARIAN).size());
        
        // Event statistics
        Map<String, Object> eventStats = eventService.getEventStatistics();
        
        // Review statistics
        Map<String, Object> reviewStats = bookReviewService.getReviewStatistics();
        
        overview.put("books", bookStats);
        overview.put("borrowings", borrowingStats);
        overview.put("users", userStats);
        overview.put("events", eventStats);
        overview.put("reviews", reviewStats);
        overview.put("lastUpdated", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        return ResponseEntity.ok(overview);
    }
    
    @GetMapping("/books/statistics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<Map<String, Object>> getBookStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        statistics.put("totalBooks", bookService.getAllBooks().size());
        statistics.put("availableBooks", bookService.getAvailableBooks().size());
        
        // Books by category
        Map<String, Integer> booksByCategory = new HashMap<>();
        for (com.enicarthage.library.entity.Book.BookCategory category : 
             com.enicarthage.library.entity.Book.BookCategory.values()) {
            booksByCategory.put(category.name(), bookService.getBooksByCategory(category).size());
        }
        statistics.put("booksByCategory", booksByCategory);
        
        // Books by status
        Map<String, Integer> booksByStatus = new HashMap<>();
        for (com.enicarthage.library.entity.Book.BookStatus status : 
             com.enicarthage.library.entity.Book.BookStatus.values()) {
            // This would need to be implemented in BookService
            booksByStatus.put(status.name(), 0); // Placeholder
        }
        statistics.put("booksByStatus", booksByStatus);
        
        return ResponseEntity.ok(statistics);
    }
    
    @GetMapping("/borrowings/statistics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<Map<String, Object>> getBorrowingStatistics() {
        Map<String, Object> statistics = borrowingService.getBorrowingStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    @GetMapping("/users/statistics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<Map<String, Object>> getUserStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        statistics.put("totalUsers", userService.getAllUsers().size());
        
        // Users by role
        Map<String, Integer> usersByRole = new HashMap<>();
        for (com.enicarthage.library.entity.User.Role role : 
             com.enicarthage.library.entity.User.Role.values()) {
            usersByRole.put(role.name(), userService.getUsersByRole(role).size());
        }
        statistics.put("usersByRole", usersByRole);
        
        // Users by status
        Map<String, Integer> usersByStatus = new HashMap<>();
        for (com.enicarthage.library.entity.User.UserStatus status : 
             com.enicarthage.library.entity.User.UserStatus.values()) {
            // This would need to be implemented in UserService
            usersByStatus.put(status.name(), 0); // Placeholder
        }
        statistics.put("usersByStatus", usersByStatus);
        
        return ResponseEntity.ok(statistics);
    }
    
    @GetMapping("/events/statistics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<Map<String, Object>> getEventStatistics() {
        Map<String, Object> statistics = eventService.getEventStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    @GetMapping("/reviews/statistics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<Map<String, Object>> getReviewStatistics() {
        Map<String, Object> statistics = bookReviewService.getReviewStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    @GetMapping("/alerts")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<Map<String, Object>> getSystemAlerts() {
        Map<String, Object> alerts = new HashMap<>();
        
        // Overdue books alert
        int overdueCount = borrowingService.getOverdueBorrowings().size();
        if (overdueCount > 0) {
            alerts.put("overdueBooks", overdueCount + " books are overdue");
        }
        
        // Low stock alert
        int lowStockCount = 0; // This would need to be implemented
        if (lowStockCount > 0) {
            alerts.put("lowStock", lowStockCount + " books have low stock");
        }
        
        // Upcoming events
        int upcomingEventsCount = eventService.getUpcomingEvents().size();
        if (upcomingEventsCount > 0) {
            alerts.put("upcomingEvents", upcomingEventsCount + " upcoming events");
        }
        
        // Unverified reviews
        Map<String, Object> reviewStats = bookReviewService.getReviewStatistics();
        int unverifiedReviews = (Integer) reviewStats.get("unverifiedReviews");
        if (unverifiedReviews > 0) {
            alerts.put("unverifiedReviews", unverifiedReviews + " reviews need verification");
        }
        
        return ResponseEntity.ok(alerts);
    }
    
    @GetMapping("/charts/borrowings-trend")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<Map<String, Object>> getBorrowingTrends(
            @RequestParam(defaultValue = "30") int days) {
        Map<String, Object> trends = new HashMap<>();
        
        // This would need to be implemented with actual data
        // For now, returning placeholder data
        trends.put("message", "Borrowing trends feature to be implemented");
        trends.put("days", days);
        
        return ResponseEntity.ok(trends);
    }
    
    @GetMapping("/charts/popular-books")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<Map<String, Object>> getPopularBooks(
            @RequestParam(defaultValue = "10") int limit) {
        Map<String, Object> popularBooks = new HashMap<>();
        
        // This would need to be implemented with actual data
        // For now, returning placeholder data
        popularBooks.put("message", "Popular books feature to be implemented");
        popularBooks.put("limit", limit);
        
        return ResponseEntity.ok(popularBooks);
    }
    
    @GetMapping("/charts/user-activity")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<Map<String, Object>> getUserActivity(
            @RequestParam(defaultValue = "30") int days) {
        Map<String, Object> activity = new HashMap<>();
        
        // This would need to be implemented with actual data
        // For now, returning placeholder data
        activity.put("message", "User activity feature to be implemented");
        activity.put("days", days);
        
        return ResponseEntity.ok(activity);
    }
}

package com.enicarthage.library.controller;

import com.enicarthage.library.entity.BookReview;
import com.enicarthage.library.service.BookReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class BookReviewController {
    
    @Autowired
    private BookReviewService bookReviewService;
    
    @GetMapping
    public ResponseEntity<Page<BookReview>> getAllReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<BookReview> reviews = bookReviewService.getAllReviews(pageable);
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BookReview> getReviewById(@PathVariable Long id) {
        Optional<BookReview> review = bookReviewService.getReviewById(id);
        return review.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/book/{bookId}")
    public ResponseEntity<Page<BookReview>> getReviewsByBook(
            @PathVariable Long bookId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<BookReview> reviews = bookReviewService.getReviewsByBook(bookId, pageable);
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ResponseEntity<List<BookReview>> getReviewsByUser(@PathVariable Long userId) {
        List<BookReview> reviews = bookReviewService.getReviewsByUser(userId);
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/my-reviews")
    @PreAuthorize("hasRole('STUDENT') or hasRole('FACULTY')")
    public ResponseEntity<List<BookReview>> getMyReviews() {
        // Get current user ID from authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof com.enicarthage.library.entity.User) {
            com.enicarthage.library.entity.User currentUser = (com.enicarthage.library.entity.User) authentication.getPrincipal();
            List<BookReview> reviews = bookReviewService.getReviewsByUser(currentUser.getId());
            return ResponseEntity.ok(reviews);
        }
        return ResponseEntity.badRequest().build();
    }
    
    @GetMapping("/book/{bookId}/average-rating")
    public ResponseEntity<Map<String, Object>> getBookAverageRating(@PathVariable Long bookId) {
        Map<String, Object> ratingInfo = bookReviewService.getBookAverageRating(bookId);
        return ResponseEntity.ok(ratingInfo);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('STUDENT') or hasRole('FACULTY')")
    public ResponseEntity<?> createReview(@Valid @RequestBody BookReview review) {
        try {
            // Set the user from the current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof com.enicarthage.library.entity.User) {
                com.enicarthage.library.entity.User currentUser = (com.enicarthage.library.entity.User) authentication.getPrincipal();
                review.setUser(currentUser);
            }
            
            BookReview createdReview = bookReviewService.createReview(review);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Review created successfully");
            response.put("review", createdReview);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('FACULTY')")
    public ResponseEntity<?> updateReview(@PathVariable Long id, @Valid @RequestBody BookReview reviewDetails) {
        try {
            // Check if the user owns this review or is an admin/librarian
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof com.enicarthage.library.entity.User) {
                com.enicarthage.library.entity.User currentUser = (com.enicarthage.library.entity.User) authentication.getPrincipal();
                
                // Check ownership (this would need to be implemented in the service)
                BookReview existingReview = bookReviewService.getReviewById(id)
                        .orElseThrow(() -> new RuntimeException("Review not found"));
                
                if (!existingReview.getUser().getId().equals(currentUser.getId()) && 
                    !currentUser.getRole().name().equals("ADMIN") && 
                    !currentUser.getRole().name().equals("LIBRARIAN")) {
                    return ResponseEntity.forbidden().build();
                }
            }
            
            BookReview updatedReview = bookReviewService.updateReview(id, reviewDetails);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Review updated successfully");
            response.put("review", updatedReview);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('FACULTY') or hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        try {
            // Check if the user owns this review or is an admin/librarian
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof com.enicarthage.library.entity.User) {
                com.enicarthage.library.entity.User currentUser = (com.enicarthage.library.entity.User) authentication.getPrincipal();
                
                BookReview existingReview = bookReviewService.getReviewById(id)
                        .orElseThrow(() -> new RuntimeException("Review not found"));
                
                if (!existingReview.getUser().getId().equals(currentUser.getId()) && 
                    !currentUser.getRole().name().equals("ADMIN") && 
                    !currentUser.getRole().name().equals("LIBRARIAN")) {
                    return ResponseEntity.forbidden().build();
                }
            }
            
            bookReviewService.deleteReview(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Review deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PatchMapping("/{id}/verify")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<?> verifyReview(@PathVariable Long id, @RequestParam Boolean isVerified) {
        try {
            BookReview updatedReview = bookReviewService.verifyReview(id, isVerified);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Review verification status updated successfully");
            response.put("review", updatedReview);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<Map<String, Object>> getReviewStatistics() {
        Map<String, Object> statistics = bookReviewService.getReviewStatistics();
        return ResponseEntity.ok(statistics);
    }
}

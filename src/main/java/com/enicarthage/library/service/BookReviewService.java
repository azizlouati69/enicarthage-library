package com.enicarthage.library.service;

import com.enicarthage.library.entity.Book;
import com.enicarthage.library.entity.BookReview;
import com.enicarthage.library.entity.User;
import com.enicarthage.library.repository.BookRepository;
import com.enicarthage.library.repository.BookReviewRepository;
import com.enicarthage.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class BookReviewService {
    
    @Autowired
    private BookReviewRepository bookReviewRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public BookReview createReview(BookReview review) {
        // Check if user has already reviewed this book
        List<BookReview> existingReviews = bookReviewRepository.findByUserAndBook(
                review.getUser(), review.getBook());
        if (!existingReviews.isEmpty()) {
            throw new RuntimeException("User has already reviewed this book");
        }
        
        // Verify that the book exists
        if (!bookRepository.existsById(review.getBook().getId())) {
            throw new RuntimeException("Book not found");
        }
        
        // Verify that the user exists
        if (!userRepository.existsById(review.getUser().getId())) {
            throw new RuntimeException("User not found");
        }
        
        // Set default values
        if (review.getIsVerified() == null) {
            review.setIsVerified(false);
        }
        
        return bookReviewRepository.save(review);
    }
    
    public BookReview updateReview(Long id, BookReview reviewDetails) {
        BookReview review = bookReviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        
        review.setReview(reviewDetails.getReview());
        review.setRating(reviewDetails.getRating());
        
        return bookReviewRepository.save(review);
    }
    
    public void deleteReview(Long id) {
        BookReview review = bookReviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        bookReviewRepository.delete(review);
    }
    
    public List<BookReview> getAllReviews() {
        return bookReviewRepository.findAll();
    }
    
    public Page<BookReview> getAllReviews(Pageable pageable) {
        return bookReviewRepository.findAll(pageable);
    }
    
    public Optional<BookReview> getReviewById(Long id) {
        return bookReviewRepository.findById(id);
    }
    
    public List<BookReview> getReviewsByBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        return bookReviewRepository.findByBook(book);
    }
    
    public Page<BookReview> getReviewsByBook(Long bookId, Pageable pageable) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        return bookReviewRepository.findByBook(book, pageable);
    }
    
    public List<BookReview> getReviewsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return bookReviewRepository.findByUser(user);
    }
    
    public List<BookReview> getReviewsByUser(User user) {
        return bookReviewRepository.findByUser(user);
    }
    
    public List<BookReview> getVerifiedReviewsByBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        return bookReviewRepository.findByBookAndIsVerified(book, true);
    }
    
    public List<BookReview> getReviewsByRating(Integer rating) {
        return bookReviewRepository.findByRating(rating);
    }
    
    public List<BookReview> getReviewsByRatingAndBook(Long bookId, Integer rating) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        return bookReviewRepository.findByBookAndRating(book, rating);
    }
    
    public BookReview verifyReview(Long id, Boolean isVerified) {
        BookReview review = bookReviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        review.setIsVerified(isVerified);
        return bookReviewRepository.save(review);
    }
    
    public Map<String, Object> getBookAverageRating(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        List<BookReview> reviews = bookReviewRepository.findByBook(book);
        
        Map<String, Object> ratingInfo = new HashMap<>();
        
        if (reviews.isEmpty()) {
            ratingInfo.put("averageRating", 0.0);
            ratingInfo.put("totalReviews", 0);
            ratingInfo.put("ratingDistribution", new HashMap<>());
            return ratingInfo;
        }
        
        // Calculate average rating
        double totalRating = reviews.stream()
                .mapToInt(BookReview::getRating)
                .sum();
        double averageRating = totalRating / reviews.size();
        
        // Calculate rating distribution
        Map<Integer, Long> ratingDistribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            long count = reviews.stream()
                    .mapToInt(BookReview::getRating)
                    .filter(rating -> rating == i)
                    .count();
            ratingDistribution.put(i, count);
        }
        
        ratingInfo.put("averageRating", Math.round(averageRating * 10.0) / 10.0);
        ratingInfo.put("totalReviews", reviews.size());
        ratingInfo.put("ratingDistribution", ratingDistribution);
        
        return ratingInfo;
    }
    
    public Map<String, Object> getReviewStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        statistics.put("totalReviews", bookReviewRepository.count());
        statistics.put("verifiedReviews", bookReviewRepository.countByIsVerified(true));
        statistics.put("unverifiedReviews", bookReviewRepository.countByIsVerified(false));
        
        // Get reviews by rating
        Map<Integer, Long> reviewsByRating = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            reviewsByRating.put(i, bookReviewRepository.countByRating(i));
        }
        statistics.put("reviewsByRating", reviewsByRating);
        
        // Calculate average rating across all reviews
        List<BookReview> allReviews = bookReviewRepository.findAll();
        if (!allReviews.isEmpty()) {
            double totalRating = allReviews.stream()
                    .mapToInt(BookReview::getRating)
                    .sum();
            double averageRating = totalRating / allReviews.size();
            statistics.put("overallAverageRating", Math.round(averageRating * 10.0) / 10.0);
        } else {
            statistics.put("overallAverageRating", 0.0);
        }
        
        return statistics;
    }
}

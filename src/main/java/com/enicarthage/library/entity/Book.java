package com.enicarthage.library.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "books")
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 200)
    private String title;
    
    @NotBlank
    @Size(max = 100)
    private String author;
    
    @Size(max = 100)
    private String isbn;
    
    @NotBlank
    @Size(max = 50)
    private String publisher;
    
    @NotNull
    @Positive
    private Integer publicationYear;
    
    @Enumerated(EnumType.STRING)
    private BookCategory category;
    
    @Enumerated(EnumType.STRING)
    private BookStatus status;
    
    @Size(max = 1000)
    @Column(length = 1000)
    private String description;
    
    @Size(max = 200)
    private String coverImageUrl;
    
    @NotNull
    @Positive
    private Integer totalCopies;
    
    @NotNull
    @Positive
    private Integer availableCopies;
    
    @Column(name = "shelf_location")
    private String shelfLocation;
    
    @Column(name = "language")
    private String language;
    
    @Column(name = "pages")
    private Integer pages;
    
    @Column(name = "price")
    private Double price;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Borrowing> borrowings;
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations;
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookReview> reviews;
    
    public Book() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = BookStatus.AVAILABLE;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    
    public Integer getPublicationYear() { return publicationYear; }
    public void setPublicationYear(Integer publicationYear) { this.publicationYear = publicationYear; }
    
    public BookCategory getCategory() { return category; }
    public void setCategory(BookCategory category) { this.category = category; }
    
    public BookStatus getStatus() { return status; }
    public void setStatus(BookStatus status) { this.status = status; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }
    
    public Integer getTotalCopies() { return totalCopies; }
    public void setTotalCopies(Integer totalCopies) { this.totalCopies = totalCopies; }
    
    public Integer getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(Integer availableCopies) { this.availableCopies = availableCopies; }
    
    public String getShelfLocation() { return shelfLocation; }
    public void setShelfLocation(String shelfLocation) { this.shelfLocation = shelfLocation; }
    
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    
    public Integer getPages() { return pages; }
    public void setPages(Integer pages) { this.pages = pages; }
    
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<Borrowing> getBorrowings() { return borrowings; }
    public void setBorrowings(List<Borrowing> borrowings) { this.borrowings = borrowings; }
    
    public List<Reservation> getReservations() { return reservations; }
    public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }
    
    public List<BookReview> getReviews() { return reviews; }
    public void setReviews(List<BookReview> reviews) { this.reviews = reviews; }
    
    public enum BookCategory {
        FICTION, NON_FICTION, SCIENCE, TECHNOLOGY, HISTORY, LITERATURE, 
        PHILOSOPHY, MATHEMATICS, PHYSICS, CHEMISTRY, BIOLOGY, MEDICINE,
        ENGINEERING, BUSINESS, ECONOMICS, POLITICS, SOCIOLOGY, PSYCHOLOGY,
        ART, MUSIC, SPORTS, TRAVEL, COOKING, REFERENCE, TEXTBOOK
    }
    
    public enum BookStatus {
        AVAILABLE, BORROWED, RESERVED, MAINTENANCE, LOST, DAMAGED
    }
}
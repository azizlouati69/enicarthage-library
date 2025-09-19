package com.enicarthage.library.service;

import com.enicarthage.library.entity.Book;
import com.enicarthage.library.entity.Borrowing;
import com.enicarthage.library.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class NotificationService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private UserService userService;
    
    private static final String FROM_EMAIL = "noreply@enicarthagelibrary.com";
    private static final String LIBRARY_NAME = "ENICARTHAGE Library";
    
    public void sendWelcomeEmail(User user) {
        String subject = "Welcome to " + LIBRARY_NAME;
        String message = String.format(
            "Dear %s %s,\n\n" +
            "Welcome to %s! Your account has been successfully created.\n\n" +
            "Account Details:\n" +
            "Username: %s\n" +
            "Email: %s\n" +
            "Role: %s\n\n" +
            "You can now access our library services and borrow books.\n\n" +
            "Best regards,\n" +
            "%s Team",
            user.getFirstName(),
            user.getLastName(),
            LIBRARY_NAME,
            user.getUsername(),
            user.getEmail(),
            user.getRole().name(),
            LIBRARY_NAME
        );
        
        sendEmail(user.getEmail(), subject, message);
    }
    
    public void sendBorrowingConfirmationEmail(User user, Book book, Borrowing borrowing) {
        String subject = "Book Borrowing Confirmation - " + book.getTitle();
        String message = String.format(
            "Dear %s %s,\n\n" +
            "Your book borrowing has been confirmed.\n\n" +
            "Book Details:\n" +
            "Title: %s\n" +
            "Author: %s\n" +
            "ISBN: %s\n\n" +
            "Borrowing Details:\n" +
            "Borrow Date: %s\n" +
            "Due Date: %s\n\n" +
            "Please return the book by the due date to avoid late fees.\n\n" +
            "Best regards,\n" +
            "%s Team",
            user.getFirstName(),
            user.getLastName(),
            book.getTitle(),
            book.getAuthor(),
            book.getIsbn(),
            borrowing.getBorrowDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            borrowing.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            LIBRARY_NAME
        );
        
        sendEmail(user.getEmail(), subject, message);
    }
    
    public void sendReturnConfirmationEmail(User user, Book book, Borrowing borrowing) {
        String subject = "Book Return Confirmation - " + book.getTitle();
        String message = String.format(
            "Dear %s %s,\n\n" +
            "Your book has been successfully returned.\n\n" +
            "Book Details:\n" +
            "Title: %s\n" +
            "Author: %s\n" +
            "ISBN: %s\n\n" +
            "Return Details:\n" +
            "Return Date: %s\n" +
            "Borrowed Date: %s\n" +
            "Due Date: %s\n" +
            "Fine Amount: $%.2f\n\n" +
            "Thank you for using our library services.\n\n" +
            "Best regards,\n" +
            "%s Team",
            user.getFirstName(),
            user.getLastName(),
            book.getTitle(),
            book.getAuthor(),
            book.getIsbn(),
            borrowing.getReturnDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            borrowing.getBorrowDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            borrowing.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            borrowing.getFineAmount() != null ? borrowing.getFineAmount() : 0.0,
            LIBRARY_NAME
        );
        
        sendEmail(user.getEmail(), subject, message);
    }
    
    public void sendOverdueReminderEmail(User user, Book book, Borrowing borrowing) {
        String subject = "Overdue Book Reminder - " + book.getTitle();
        String message = String.format(
            "Dear %s %s,\n\n" +
            "This is a reminder that you have an overdue book.\n\n" +
            "Book Details:\n" +
            "Title: %s\n" +
            "Author: %s\n" +
            "ISBN: %s\n\n" +
            "Borrowing Details:\n" +
            "Borrow Date: %s\n" +
            "Due Date: %s\n" +
            "Days Overdue: %d\n" +
            "Current Fine: $%.2f\n\n" +
            "Please return the book as soon as possible to avoid additional fines.\n\n" +
            "Best regards,\n" +
            "%s Team",
            user.getFirstName(),
            user.getLastName(),
            book.getTitle(),
            book.getAuthor(),
            book.getIsbn(),
            borrowing.getBorrowDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            borrowing.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            java.time.temporal.ChronoUnit.DAYS.between(borrowing.getDueDate(), LocalDateTime.now()),
            borrowing.getFineAmount() != null ? borrowing.getFineAmount() : 0.0,
            LIBRARY_NAME
        );
        
        sendEmail(user.getEmail(), subject, message);
    }
    
    public void sendDueDateReminderEmail(User user, Book book, Borrowing borrowing) {
        String subject = "Book Due Date Reminder - " + book.getTitle();
        String message = String.format(
            "Dear %s %s,\n\n" +
            "This is a friendly reminder that your book is due soon.\n\n" +
            "Book Details:\n" +
            "Title: %s\n" +
            "Author: %s\n" +
            "ISBN: %s\n\n" +
            "Borrowing Details:\n" +
            "Borrow Date: %s\n" +
            "Due Date: %s\n" +
            "Days Remaining: %d\n\n" +
            "Please return or renew the book by the due date.\n\n" +
            "Best regards,\n" +
            "%s Team",
            user.getFirstName(),
            user.getLastName(),
            book.getTitle(),
            book.getAuthor(),
            book.getIsbn(),
            borrowing.getBorrowDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            borrowing.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            java.time.temporal.ChronoUnit.DAYS.between(LocalDateTime.now(), borrowing.getDueDate()),
            LIBRARY_NAME
        );
        
        sendEmail(user.getEmail(), subject, message);
    }
    
    public void sendEventReminderEmail(User user, com.enicarthage.library.entity.Event event) {
        String subject = "Event Reminder - " + event.getTitle();
        String message = String.format(
            "Dear %s %s,\n\n" +
            "This is a reminder about the upcoming library event.\n\n" +
            "Event Details:\n" +
            "Title: %s\n" +
            "Description: %s\n" +
            "Date: %s\n" +
            "Location: %s\n" +
            "Type: %s\n\n" +
            "We look forward to seeing you at the event!\n\n" +
            "Best regards,\n" +
            "%s Team",
            user.getFirstName(),
            user.getLastName(),
            event.getTitle(),
            event.getDescription(),
            event.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            event.getLocation(),
            event.getType().name(),
            LIBRARY_NAME
        );
        
        sendEmail(user.getEmail(), subject, message);
    }
    
    public void sendPasswordResetEmail(User user, String resetToken) {
        String subject = "Password Reset Request - " + LIBRARY_NAME;
        String message = String.format(
            "Dear %s %s,\n\n" +
            "You have requested to reset your password.\n\n" +
            "Please click the following link to reset your password:\n" +
            "http://localhost:4200/reset-password?token=%s\n\n" +
            "This link will expire in 24 hours.\n\n" +
            "If you did not request this password reset, please ignore this email.\n\n" +
            "Best regards,\n" +
            "%s Team",
            user.getFirstName(),
            user.getLastName(),
            resetToken,
            LIBRARY_NAME
        );
        
        sendEmail(user.getEmail(), subject, message);
    }
    
    public void sendAccountStatusChangeEmail(User user, String status) {
        String subject = "Account Status Update - " + LIBRARY_NAME;
        String message = String.format(
            "Dear %s %s,\n\n" +
            "Your account status has been updated to: %s\n\n" +
            "If you have any questions about this change, please contact the library staff.\n\n" +
            "Best regards,\n" +
            "%s Team",
            user.getFirstName(),
            user.getLastName(),
            status,
            LIBRARY_NAME
        );
        
        sendEmail(user.getEmail(), subject, message);
    }
    
    public void sendBulkOverdueReminders(List<Borrowing> overdueBorrowings) {
        for (Borrowing borrowing : overdueBorrowings) {
            try {
                sendOverdueReminderEmail(borrowing.getUser(), borrowing.getBook(), borrowing);
                Thread.sleep(1000); // Rate limiting
            } catch (Exception e) {
                // Log error but continue with other emails
                System.err.println("Failed to send overdue reminder to: " + borrowing.getUser().getEmail());
            }
        }
    }
    
    public void sendBulkDueDateReminders(List<Borrowing> dueSoonBorrowings) {
        for (Borrowing borrowing : dueSoonBorrowings) {
            try {
                sendDueDateReminderEmail(borrowing.getUser(), borrowing.getBook(), borrowing);
                Thread.sleep(1000); // Rate limiting
            } catch (Exception e) {
                // Log error but continue with other emails
                System.err.println("Failed to send due date reminder to: " + borrowing.getUser().getEmail());
            }
        }
    }
    
    private void sendEmail(String to, String subject, String message) {
        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setFrom(FROM_EMAIL);
            email.setTo(to);
            email.setSubject(subject);
            email.setText(message);
            
            mailSender.send(email);
            System.out.println("Email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("Failed to send email to: " + to + " - " + e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }
}

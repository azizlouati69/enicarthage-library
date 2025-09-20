package com.enicarthage.library.controller;

import com.enicarthage.library.entity.User;
import com.enicarthage.library.service.UserService;
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
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Note: This would need to be implemented in UserService
        // For now, returning all users without pagination
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok().build(); // Placeholder
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN') or @userSecurityService.isOwner(authentication, #id)")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/profile")
    @PreAuthorize("hasRole('STUDENT') or hasRole('FACULTY') or hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ResponseEntity<User> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.badRequest().build();
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN') or @userSecurityService.isOwner(authentication, #id)")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User updated successfully");
            response.put("user", updatedUser);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PutMapping("/profile")
    @PreAuthorize("hasRole('STUDENT') or hasRole('FACULTY') or hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ResponseEntity<?> updateCurrentUserProfile(@Valid @RequestBody User userDetails) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                User currentUser = (User) authentication.getPrincipal();
                User updatedUser = userService.updateUser(currentUser.getId(), userDetails);
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Profile updated successfully");
                response.put("user", updatedUser);
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.badRequest().body("User not authenticated");
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String name) {
        List<User> users = userService.searchUsersByName(name);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable User.Role role) {
        List<User> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }
    
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<?> updateUserStatus(@PathVariable Long id, @RequestParam User.UserStatus status) {
        try {
            User user = userService.getUserById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setStatus(status);
            User updatedUser = userService.updateUser(id, user);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User status updated successfully");
            response.put("user", updatedUser);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestParam User.Role role) {
        try {
            User user = userService.getUserById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setRole(role);
            User updatedUser = userService.updateUser(id, user);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User role updated successfully");
            response.put("user", updatedUser);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/{id}/statistics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN') or @userSecurityService.isOwner(authentication, #id)")
    public ResponseEntity<Map<String, Object>> getUserStatistics(@PathVariable Long id) {
        // This would need to be implemented in UserService
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("userId", id);
        statistics.put("message", "Statistics feature to be implemented");
        return ResponseEntity.ok(statistics);
    }
}

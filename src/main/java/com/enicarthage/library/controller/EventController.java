package com.enicarthage.library.controller;

import com.enicarthage.library.entity.Event;
import com.enicarthage.library.service.EventService;
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
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {
    
    @Autowired
    private EventService eventService;
    
    @GetMapping
    public ResponseEntity<Page<Event>> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Event> events = eventService.getAllEvents(pageable);
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        Optional<Event> event = eventService.getEventById(id);
        return event.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/upcoming")
    public ResponseEntity<List<Event>> getUpcomingEvents() {
        List<Event> events = eventService.getUpcomingEvents();
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/ongoing")
    public ResponseEntity<List<Event>> getOngoingEvents() {
        List<Event> events = eventService.getOngoingEvents();
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Event>> getEventsByType(@PathVariable Event.EventType type) {
        List<Event> events = eventService.getEventsByType(type);
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<Event>> searchEvents(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> events = eventService.searchEvents(q, pageable);
        return ResponseEntity.ok(events);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<?> createEvent(@Valid @RequestBody Event event) {
        try {
            // Set the creator from the current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof com.enicarthage.library.entity.User) {
                com.enicarthage.library.entity.User currentUser = (com.enicarthage.library.entity.User) authentication.getPrincipal();
                event.setCreatedBy(currentUser);
            }
            
            Event createdEvent = eventService.createEvent(event);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Event created successfully");
            response.put("event", createdEvent);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @Valid @RequestBody Event eventDetails) {
        try {
            Event updatedEvent = eventService.updateEvent(id, eventDetails);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Event updated successfully");
            response.put("event", updatedEvent);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        try {
            eventService.deleteEvent(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Event deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<?> updateEventStatus(@PathVariable Long id, @RequestParam Event.EventStatus status) {
        try {
            Event updatedEvent = eventService.updateEventStatus(id, status);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Event status updated successfully");
            response.put("event", updatedEvent);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/{id}/register")
    @PreAuthorize("hasRole('STUDENT') or hasRole('FACULTY')")
    public ResponseEntity<?> registerForEvent(@PathVariable Long id) {
        try {
            // Get current user ID from authentication
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof com.enicarthage.library.entity.User) {
                com.enicarthage.library.entity.User currentUser = (com.enicarthage.library.entity.User) authentication.getPrincipal();
                eventService.registerForEvent(id, currentUser.getId());
                Map<String, String> response = new HashMap<>();
                response.put("message", "Successfully registered for event");
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.badRequest().body("User not authenticated");
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @DeleteMapping("/{id}/unregister")
    @PreAuthorize("hasRole('STUDENT') or hasRole('FACULTY')")
    public ResponseEntity<?> unregisterFromEvent(@PathVariable Long id) {
        try {
            // Get current user ID from authentication
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof com.enicarthage.library.entity.User) {
                com.enicarthage.library.entity.User currentUser = (com.enicarthage.library.entity.User) authentication.getPrincipal();
                eventService.unregisterFromEvent(id, currentUser.getId());
                Map<String, String> response = new HashMap<>();
                response.put("message", "Successfully unregistered from event");
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.badRequest().body("User not authenticated");
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<Map<String, Object>> getEventStatistics() {
        Map<String, Object> statistics = eventService.getEventStatistics();
        return ResponseEntity.ok(statistics);
    }
}

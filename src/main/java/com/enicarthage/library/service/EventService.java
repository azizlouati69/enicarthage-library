package com.enicarthage.library.service;

import com.enicarthage.library.entity.Event;
import com.enicarthage.library.entity.User;
import com.enicarthage.library.repository.EventRepository;
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
public class EventService {
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public Event createEvent(Event event) {
        // Validate event dates
        if (event.getStartDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Event start date cannot be in the past");
        }
        if (event.getEndDate().isBefore(event.getStartDate())) {
            throw new RuntimeException("Event end date cannot be before start date");
        }
        
        // Set default values
        if (event.getCurrentAttendees() == null) {
            event.setCurrentAttendees(0);
        }
        if (event.getRegistrationRequired() == null) {
            event.setRegistrationRequired(false);
        }
        
        // Auto-update status based on dates
        updateEventStatus(event);
        
        return eventRepository.save(event);
    }
    
    public Event updateEvent(Long id, Event eventDetails) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        // Validate event dates
        if (eventDetails.getStartDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Event start date cannot be in the past");
        }
        if (eventDetails.getEndDate().isBefore(eventDetails.getStartDate())) {
            throw new RuntimeException("Event end date cannot be before start date");
        }
        
        event.setTitle(eventDetails.getTitle());
        event.setDescription(eventDetails.getDescription());
        event.setStartDate(eventDetails.getStartDate());
        event.setEndDate(eventDetails.getEndDate());
        event.setLocation(eventDetails.getLocation());
        event.setType(eventDetails.getType());
        event.setMaxAttendees(eventDetails.getMaxAttendees());
        event.setImageUrl(eventDetails.getImageUrl());
        event.setRegistrationRequired(eventDetails.getRegistrationRequired());
        event.setRegistrationDeadline(eventDetails.getRegistrationDeadline());
        event.setContactInfo(eventDetails.getContactInfo());
        
        // Auto-update status based on dates
        updateEventStatus(event);
        
        return eventRepository.save(event);
    }
    
    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        eventRepository.delete(event);
    }
    
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
    
    public Page<Event> getAllEvents(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }
    
    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }
    
    public List<Event> getUpcomingEvents() {
        return eventRepository.findUpcomingEvents(LocalDateTime.now());
    }
    
    public List<Event> getOngoingEvents() {
        LocalDateTime now = LocalDateTime.now();
        return eventRepository.findOngoingEvents(now, now);
    }
    
    public List<Event> getEventsByType(Event.EventType type) {
        return eventRepository.findByType(type);
    }
    
    public List<Event> searchEvents(String searchTerm) {
        return eventRepository.findBySearchTerm(searchTerm);
    }
    
    public Page<Event> searchEvents(String searchTerm, Pageable pageable) {
        return eventRepository.findBySearchTerm(searchTerm, pageable);
    }
    
    public Event updateEventStatus(Long id, Event.EventStatus status) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        event.setStatus(status);
        return eventRepository.save(event);
    }
    
    public void registerForEvent(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if registration is required
        if (!event.getRegistrationRequired()) {
            throw new RuntimeException("Registration is not required for this event");
        }
        
        // Check registration deadline
        if (event.getRegistrationDeadline() != null && 
            LocalDateTime.now().isAfter(event.getRegistrationDeadline())) {
            throw new RuntimeException("Registration deadline has passed");
        }
        
        // Check if event has reached maximum attendees
        if (event.getMaxAttendees() != null && 
            event.getCurrentAttendees() >= event.getMaxAttendees()) {
            throw new RuntimeException("Event has reached maximum attendees");
        }
        
        // Check if user is already registered (this would need a separate EventRegistration entity)
        // For now, we'll just increment the attendee count
        event.setCurrentAttendees(event.getCurrentAttendees() + 1);
        eventRepository.save(event);
    }
    
    public void unregisterFromEvent(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        // Decrement attendee count (this would need proper tracking in a real implementation)
        if (event.getCurrentAttendees() > 0) {
            event.setCurrentAttendees(event.getCurrentAttendees() - 1);
            eventRepository.save(event);
        }
    }
    
    public Map<String, Object> getEventStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        statistics.put("totalEvents", eventRepository.count());
        statistics.put("upcomingEvents", eventRepository.countByStatus(Event.EventStatus.UPCOMING));
        statistics.put("ongoingEvents", eventRepository.countByStatus(Event.EventStatus.ONGOING));
        statistics.put("completedEvents", eventRepository.countByStatus(Event.EventStatus.COMPLETED));
        statistics.put("cancelledEvents", eventRepository.countByStatus(Event.EventStatus.CANCELLED));
        
        // Get events by type
        Map<String, Long> eventsByType = new HashMap<>();
        for (Event.EventType type : Event.EventType.values()) {
            eventsByType.put(type.name(), eventRepository.countByType(type));
        }
        statistics.put("eventsByType", eventsByType);
        
        return statistics;
    }
    
    private void updateEventStatus(Event event) {
        LocalDateTime now = LocalDateTime.now();
        
        if (now.isBefore(event.getStartDate())) {
            event.setStatus(Event.EventStatus.UPCOMING);
        } else if (now.isAfter(event.getStartDate()) && now.isBefore(event.getEndDate())) {
            event.setStatus(Event.EventStatus.ONGOING);
        } else if (now.isAfter(event.getEndDate())) {
            event.setStatus(Event.EventStatus.COMPLETED);
        }
    }
}

package com.enicarthage.library.repository;

import com.enicarthage.library.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    List<Event> findByType(Event.EventType type);
    
    List<Event> findByStatus(Event.EventStatus status);
    
    List<Event> findByLocationContainingIgnoreCase(String location);
    
    @Query("SELECT e FROM Event e WHERE e.startDate >= :currentDate ORDER BY e.startDate ASC")
    List<Event> findUpcomingEvents(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT e FROM Event e WHERE e.startDate <= :currentDate AND e.endDate >= :currentDate")
    List<Event> findOngoingEvents(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT e FROM Event e WHERE e.endDate < :currentDate ORDER BY e.endDate DESC")
    List<Event> findPastEvents(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT e FROM Event e WHERE e.startDate BETWEEN :startDate AND :endDate")
    List<Event> findEventsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT e FROM Event e WHERE e.title LIKE %:searchTerm% OR e.description LIKE %:searchTerm%")
    List<Event> findBySearchTerm(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT e FROM Event e WHERE e.registrationRequired = true AND e.registrationDeadline > :currentDate")
    List<Event> findEventsWithOpenRegistration(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT e FROM Event e WHERE e.maxAttendees IS NOT NULL AND e.currentAttendees < e.maxAttendees")
    List<Event> findEventsWithAvailableSpots();
    
    @Query("SELECT e FROM Event e WHERE e.title LIKE %:searchTerm% OR e.description LIKE %:searchTerm%")
    Page<Event> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    Long countByStatus(Event.EventStatus status);
    
    Long countByType(Event.EventType type);
    
    @Query("SELECT e FROM Event e WHERE e.startDate <= :startDate AND e.endDate >= :endDate")
    List<Event> findOngoingEvents(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
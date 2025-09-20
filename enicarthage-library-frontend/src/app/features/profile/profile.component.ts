import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BookService } from '../../core/services/book.service';
import { EventService, EventItem } from '../../core/services/event.service';
import { Book } from '../../core/models/book.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  activeTab: 'books' | 'events' = 'books';

  availableBooks: Book[] = [];
  upcomingEvents: EventItem[] = [];

  isLoadingBooks = false;
  isLoadingEvents = false;

  constructor(private bookService: BookService, private eventService: EventService) {}

  ngOnInit(): void {
    this.loadBooks();
    this.loadEvents();
  }

  loadBooks(): void {
    this.isLoadingBooks = true;
    this.bookService.getAvailable().subscribe({
      next: (list) => { this.availableBooks = list; this.isLoadingBooks = false; },
      error: () => { this.isLoadingBooks = false; }
    });
  }

  loadEvents(): void {
    this.isLoadingEvents = true;
    this.eventService.getUpcoming().subscribe({
      next: (list) => { this.upcomingEvents = list; this.isLoadingEvents = false; },
      error: () => { this.isLoadingEvents = false; }
    });
  }

  showTab(tab: 'books' | 'events'): void {
    this.activeTab = tab;
  }
}

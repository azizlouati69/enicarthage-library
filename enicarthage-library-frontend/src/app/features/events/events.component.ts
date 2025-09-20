import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EventService, PagedResponse, EventItem } from '../../core/services/event.service';

@Component({
  selector: 'app-events',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.scss']
})
export class EventsComponent implements OnInit {
  events: EventItem[] = [];
  isLoading = false;

  searchTerm = '';
  selectedStatus: 'upcoming' | 'past' | 'cancelled' | 'all' = 'all';

  pageSize = 10;
  currentPage = 0;
  totalItems = 0;

  constructor(private eventService: EventService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.isLoading = true;
    const page = this.currentPage; const size = this.pageSize;

    const handleResp = (resp: PagedResponse<EventItem>) => {
      this.events = resp.content;
      this.totalItems = resp.totalElements;
      this.isLoading = false;
    };
    const handleErr = () => { this.isLoading = false; };

    // Use search endpoint when searchTerm is present
    if (this.searchTerm.trim()) {
      this.eventService.search(this.searchTerm.trim(), page, size).subscribe({ next: handleResp, error: handleErr });
    } else {
      // Sort by startDate ascending to mimic screenshot
      this.eventService.getAll(page, size, 'startDate', 'asc').subscribe({ next: handleResp, error: handleErr });
    }
  }

  onSearch(): void {
    this.currentPage = 0;
    this.load();
  }

  onStatusChange(): void {
    // Client-side filter on status for now since backend status filter is via endpoints
    // If needed, we could call specific endpoints (upcoming/ongoing) here
  }

  getPagedEvents(): EventItem[] {
    // Apply local status filter for UI parity
    let list = this.events;
    if (this.selectedStatus !== 'all') {
      list = list.filter(e => {
        if (this.selectedStatus === 'upcoming') return (e.status || '').toLowerCase() === 'upcoming';
        if (this.selectedStatus === 'past') return (e.status || '').toLowerCase() === 'past';
        if (this.selectedStatus === 'cancelled') return (e.status || '').toLowerCase() === 'cancelled';
        return true;
      });
    }
    return list;
  }

  onPagePrev(): void { if (this.currentPage > 0) { this.currentPage--; this.load(); } }
  onPageNext(): void { if ((this.currentPage + 1) * this.pageSize < this.totalItems) { this.currentPage++; this.load(); } }
}

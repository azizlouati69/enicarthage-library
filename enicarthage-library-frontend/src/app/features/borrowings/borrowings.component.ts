import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BorrowingService, PagedResponse, BorrowingItem } from '../../core/services/borrowing.service';

@Component({
  selector: 'app-borrowings',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './borrowings.component.html',
  styleUrls: ['./borrowings.component.scss']
})
export class BorrowingsComponent implements OnInit {
  rows: BorrowingItem[] = [];
  isLoading = false;

  searchTerm = '';
  selectedStatus: 'all' | 'active' | 'overdue' | 'returned' = 'all';

  pageSize = 10;
  currentPage = 0;
  totalItems = 0;

  constructor(private borrowingService: BorrowingService) {}

  ngOnInit(): void { this.load(); }

  load(): void {
    this.isLoading = true;
    const page = this.currentPage; const size = this.pageSize;
    const onSuccess = (resp: PagedResponse<BorrowingItem>) => {
      this.rows = resp.content;
      this.totalItems = resp.totalElements;
      this.isLoading = false;
    };
    const onError = () => { this.isLoading = false; };

    // Backend doesn't provide a general search for borrowings in our controller; use getAll
    this.borrowingService.getAll(page, size, 'borrowDate', 'desc').subscribe({ next: onSuccess, error: onError });
  }

  getFilteredRows(): BorrowingItem[] {
    let list = this.rows;
    if (this.selectedStatus !== 'all') {
      list = list.filter(r => (r.status || '').toLowerCase() === this.selectedStatus);
    }
    if (this.searchTerm.trim()) {
      const q = this.searchTerm.trim().toLowerCase();
      // Without join data, we can only filter by id / dates client-side as a placeholder
      list = list.filter(r =>
        String(r.id).includes(q) ||
        (r.borrowDate || '').toLowerCase().includes(q) ||
        (r.returnDate || '').toLowerCase().includes(q)
      );
    }
    return list;
  }

  onSearch(): void { this.currentPage = 0; this.load(); }
  onStatusChange(): void { /* local filter only for UI */ }
  onPagePrev(): void { if (this.currentPage > 0) { this.currentPage--; this.load(); } }
  onPageNext(): void { if ((this.currentPage + 1) * this.pageSize < this.totalItems) { this.currentPage++; this.load(); } }

  onReturn(row: BorrowingItem): void {
    // Hook up to return API
    this.borrowingService.returnBook(row.id).subscribe(() => this.load());
  }
}

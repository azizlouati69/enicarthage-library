import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatChipsModule } from '@angular/material/chips';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Book, BookCategory, BookStatus } from '../../core/models/book.model';
import { FormsModule } from '@angular/forms';
import { BookService, PagedResponse } from '../../core/services/book.service';

@Component({
  selector: 'app-books',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatChipsModule,
    MatPaginatorModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './books.component.html',
  styleUrls: ['./books.component.scss']
})
export class BooksComponent implements OnInit {
  books: Book[] = [];
  filteredBooks: Book[] = [];
  isLoading = false;
  searchTerm = '';
  selectedCategory: BookCategory | null = null;
  selectedStatus: BookStatus | null = null;
  
  // Pagination
  pageSize = 12;
  currentPage = 0;
  totalItems = 0;
  
  // Categories for filter
  categories = Object.values(BookCategory);
  statuses = Object.values(BookStatus);

  constructor(private bookService: BookService) {}

  ngOnInit() {
    this.loadBooks();
  }

  loadBooks() {
    this.isLoading = true;
    // If there's a search term, use the search API; otherwise fetch paginated list
    const page = this.currentPage;
    const size = this.pageSize;

    const onSuccess = (resp: PagedResponse<Book>) => {
      this.books = resp.content;
      this.filteredBooks = this.applyLocalFilters(this.books);
      this.totalItems = resp.totalElements;
      this.isLoading = false;
    };

    const onError = () => { this.isLoading = false; };

    if (this.searchTerm && this.searchTerm.trim().length > 0) {
      this.bookService.search(this.searchTerm.trim(), { page, size }).subscribe({ next: onSuccess, error: onError });
    } else {
      this.bookService.getAll({ page, size }).subscribe({ next: onSuccess, error: onError });
    }
  }

  private applyLocalFilters(source: Book[]): Book[] {
    return source.filter(book => {
      const matchesCategory = !this.selectedCategory || book.category === this.selectedCategory;
      const matchesStatus = !this.selectedStatus || book.status === this.selectedStatus;
      return matchesCategory && matchesStatus;
    });
  }

  applyFilters() {
    // When filters change, reload current page and apply local filters for category/status
    this.currentPage = 0;
    this.loadBooks();
  }

  onSearch() {
    this.currentPage = 0;
    this.loadBooks();
  }

  onCategoryChange() {
    this.applyFilters();
  }

  onStatusChange() {
    this.applyFilters();
  }

  clearFilters() {
    this.searchTerm = '';
    this.selectedCategory = null;
    this.selectedStatus = null;
    this.currentPage = 0;
    this.loadBooks();
  }

  onPageChange(event: PageEvent) {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadBooks();
  }

  getPaginatedBooks(): Book[] {
    // Server returns current page; we already loaded it in books -> filteredBooks
    return this.filteredBooks;
  }

  getStatusColor(status: BookStatus): string {
    const colors: { [key in BookStatus]: string } = {
      'AVAILABLE': 'primary',
      'BORROWED': 'accent',
      'RESERVED': 'warn',
      'MAINTENANCE': 'warn',
      'LOST': 'warn',
      'DAMAGED': 'warn'
    };
    return colors[status] || 'primary';
  }

  borrowBook(book: Book) {
    // Implement borrow functionality
    console.log('Borrowing book:', book.title);
  }

  reserveBook(book: Book) {
    // Implement reserve functionality
    console.log('Reserving book:', book.title);
  }
}
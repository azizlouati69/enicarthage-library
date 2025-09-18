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

@Component({
  selector: 'app-books',
  standalone: true,
  imports: [
    CommonModule,
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

  constructor() {}

  ngOnInit() {
    this.loadBooks();
  }

  loadBooks() {
    this.isLoading = true;
    // Mock data - in real app, this would be an API call
    setTimeout(() => {
      this.books = this.generateMockBooks();
      this.applyFilters();
      this.isLoading = false;
    }, 1000);
  }

  generateMockBooks(): Book[] {
    const mockBooks: Book[] = [];
    const titles = [
      'Introduction to Algorithms', 'Clean Code', 'Design Patterns',
      'The Pragmatic Programmer', 'System Design Interview',
      'JavaScript: The Good Parts', 'Python Crash Course',
      'Data Structures and Algorithms', 'Machine Learning Yearning',
      'Deep Learning', 'Artificial Intelligence: A Modern Approach',
      'Computer Networks', 'Operating System Concepts',
      'Database System Concepts', 'Software Engineering',
      'Computer Graphics', 'Cryptography and Network Security',
      'Computer Organization and Design', 'Advanced Programming',
      'Web Development with React', 'Mobile App Development',
      'Cloud Computing', 'DevOps Handbook', 'Agile Software Development'
    ];
    
    const authors = [
      'Thomas H. Cormen', 'Robert C. Martin', 'Gang of Four',
      'David Thomas', 'Alex Xu', 'Douglas Crockford',
      'Eric Matthes', 'Mark Allen Weiss', 'Andrew Ng',
      'Ian Goodfellow', 'Stuart Russell', 'Andrew Tanenbaum',
      'Abraham Silberschatz', 'Raghu Ramakrishnan', 'Ian Sommerville',
      'Peter Shirley', 'William Stallings', 'David Patterson',
      'Bjarne Stroustrup', 'Dan Abramov', 'Maxime Blanchard',
      'Thomas Erl', 'Gene Kim', 'Robert C. Martin'
    ];

    for (let i = 0; i < 50; i++) {
      mockBooks.push({
        id: i + 1,
        title: titles[i % titles.length],
        author: authors[i % authors.length],
        isbn: `978-0-${Math.floor(Math.random() * 900000) + 100000}-${Math.floor(Math.random() * 90) + 10}-${Math.floor(Math.random() * 9) + 1}`,
        publisher: 'Tech Publishers',
        publicationYear: 2020 + (i % 4),
        category: this.categories[i % this.categories.length] as BookCategory,
        status: this.statuses[i % this.statuses.length] as BookStatus,
        description: 'A comprehensive guide to modern software development practices and methodologies.',
        coverImageUrl: `https://picsum.photos/200/300?random=${i}`,
        totalCopies: Math.floor(Math.random() * 10) + 1,
        availableCopies: Math.floor(Math.random() * 5) + 1,
        shelfLocation: `A${Math.floor(Math.random() * 9) + 1}-${Math.floor(Math.random() * 9) + 1}`,
        language: 'English',
        pages: Math.floor(Math.random() * 500) + 200,
        price: Math.floor(Math.random() * 100) + 20,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      });
    }
    
    return mockBooks;
  }

  applyFilters() {
    this.filteredBooks = this.books.filter(book => {
      const matchesSearch = !this.searchTerm || 
        book.title.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        book.author.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        book.isbn?.toLowerCase().includes(this.searchTerm.toLowerCase());
      
      const matchesCategory = !this.selectedCategory || book.category === this.selectedCategory;
      const matchesStatus = !this.selectedStatus || book.status === this.selectedStatus;
      
      return matchesSearch && matchesCategory && matchesStatus;
    });
    
    this.totalItems = this.filteredBooks.length;
    this.currentPage = 0;
  }

  onSearch() {
    this.applyFilters();
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
    this.applyFilters();
  }

  onPageChange(event: PageEvent) {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
  }

  getPaginatedBooks(): Book[] {
    const startIndex = this.currentPage * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    return this.filteredBooks.slice(startIndex, endIndex);
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
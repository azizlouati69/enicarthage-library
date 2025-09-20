import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService, UserItem } from '../../core/services/user.service';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss']
})
export class UsersComponent implements OnInit {
  rows: UserItem[] = [];
  isLoading = false;

  searchTerm = '';
  selectedRole: 'all' | 'ADMIN' | 'LIBRARIAN' | 'STUDENT' | 'FACULTY' = 'all';

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.isLoading = true;
    const onDone = () => (this.isLoading = false);

    if (this.searchTerm.trim()) {
      this.userService.searchByName(this.searchTerm.trim()).subscribe({
        next: (list) => { this.rows = list; onDone(); },
        error: () => onDone()
      });
      return;
    }

    // Fallback to getAll() since backend pagination is placeholder
    this.userService.getAll().subscribe({
      next: (list) => { this.rows = list; onDone(); },
      error: () => onDone()
    });
  }

  onSearch(): void { this.load(); }

  getFilteredRows(): UserItem[] {
    if (this.selectedRole === 'all') return this.rows;
    return this.rows.filter(u => u.role === this.selectedRole);
  }
}

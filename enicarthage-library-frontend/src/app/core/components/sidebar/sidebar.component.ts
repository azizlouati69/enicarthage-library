import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { AuthService } from '../../services/auth.service';
import { User, UserRole } from '../../models/user.model';

interface MenuItem {
  label: string;
  icon: string;
  route: string;
  roles?: UserRole[];
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
    MatDividerModule
  ],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {
  currentUser: User | null = null;
  isOpen = true;

  menuItems: MenuItem[] = [
    {
      label: 'Dashboard',
      icon: 'dashboard',
      route: '/dashboard',
      roles: [UserRole.ADMIN, UserRole.LIBRARIAN, UserRole.STUDENT, UserRole.FACULTY]
    },
    {
      label: 'Books',
      icon: 'library_books',
      route: '/books',
      roles: [UserRole.ADMIN, UserRole.LIBRARIAN, UserRole.STUDENT, UserRole.FACULTY]
    },
    {
      label: 'Users',
      icon: 'people',
      route: '/users',
      roles: [UserRole.ADMIN, UserRole.LIBRARIAN]
    },
    {
      label: 'My Borrowings',
      icon: 'book_online',
      route: '/borrowings',
      roles: [UserRole.STUDENT, UserRole.FACULTY]
    },
    {
      label: 'Events',
      icon: 'event',
      route: '/events',
      roles: [UserRole.ADMIN, UserRole.LIBRARIAN, UserRole.STUDENT, UserRole.FACULTY]
    },
    {
      label: 'Profile',
      icon: 'person',
      route: '/profile',
      roles: [UserRole.ADMIN, UserRole.LIBRARIAN, UserRole.STUDENT, UserRole.FACULTY]
    },
    {
      label: 'Admin Panel',
      icon: 'admin_panel_settings',
      route: '/admin',
      roles: [UserRole.ADMIN, UserRole.LIBRARIAN]
    }
  ];

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
  }

  get filteredMenuItems(): MenuItem[] {
    if (!this.currentUser) return [];
    
    return this.menuItems.filter(item => 
      !item.roles || item.roles.includes(this.currentUser!.role)
    );
  }

  toggleSidebar() {
    this.isOpen = !this.isOpen;
  }

  isActiveRoute(route: string): boolean {
    return this.router.url === route;
  }
}
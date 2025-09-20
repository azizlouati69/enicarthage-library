import { Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';
import { RoleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/books',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'books',
    loadComponent: () => import('./features/books/books.component').then(m => m.BooksComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'books/:id',
    loadComponent: () => import('./features/books/book-detail/book-detail.component').then(m => m.BookDetailComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'borrowings',
    loadComponent: () => import('./features/borrowings/borrowings.component').then(m => m.BorrowingsComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'events',
    loadComponent: () => import('./features/events/events.component').then(m => m.EventsComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'events/:id',
    loadComponent: () => import('./features/events/event-detail/event-detail.component').then(m => m.EventDetailComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'profile',
    loadComponent: () => import('./features/profile/profile.component').then(m => m.ProfileComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'admin',
    loadComponent: () => import('./features/admin/admin.component').then(m => m.AdminComponent),
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN', 'LIBRARIAN'] }
  },
  {
    path: 'users',
    loadComponent: () => import('./features/users/users.component').then(m => m.UsersComponent),
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN', 'LIBRARIAN'] }
  },
  {
    path: '**',
    loadComponent: () => import('./shared/components/not-found/not-found.component').then(m => m.NotFoundComponent)
  }
];
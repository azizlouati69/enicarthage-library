import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface UserItem {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: 'ADMIN' | 'LIBRARIAN' | 'STUDENT' | 'FACULTY';
  status: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED';
  createdAt?: string;
}

export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly API = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  // Note: backend pagination for /api/users is a placeholder
  getAll(): Observable<UserItem[]> {
    return this.http.get<UserItem[]>(this.API);
  }

  getById(id: number): Observable<UserItem> {
    return this.http.get<UserItem>(`${this.API}/${id}`);
  }

  searchByName(name: string): Observable<UserItem[]> {
    const params = new HttpParams().set('name', name);
    return this.http.get<UserItem[]>(`${this.API}/search`, { params });
  }

  getByRole(role: string): Observable<UserItem[]> {
    return this.http.get<UserItem[]>(`${this.API}/role/${role}`);
  }

  update(id: number, payload: Partial<UserItem>): Observable<any> {
    return this.http.put(`${this.API}/${id}`, payload);
  }

  updateStatus(id: number, status: string): Observable<any> {
    const params = new HttpParams().set('status', status);
    return this.http.patch(`${this.API}/${id}/status`, null, { params });
  }

  updateRole(id: number, role: string): Observable<any> {
    const params = new HttpParams().set('role', role);
    return this.http.patch(`${this.API}/${id}/role`, null, { params });
  }
}

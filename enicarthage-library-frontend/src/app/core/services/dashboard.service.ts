import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface DashboardOverview {
  books?: { totalBooks?: number; availableBooks?: number };
  borrowings?: Record<string, any>;
  users?: { totalUsers?: number };
  events?: Record<string, any>;
  reviews?: Record<string, any>;
  lastUpdated?: string;
}

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private readonly API = `${environment.apiUrl}/dashboard`;

  constructor(private http: HttpClient) {}

  getOverview(): Observable<DashboardOverview> {
    return this.http.get<DashboardOverview>(`${this.API}/overview`);
  }
}

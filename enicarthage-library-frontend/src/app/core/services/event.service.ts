import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface EventItem {
  id: number;
  name: string;
  description?: string;
  location?: string;
  startDate: string;
  endDate?: string;
  type?: string;
  status?: string;
  createdBy?: any;
}

export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

@Injectable({ providedIn: 'root' })
export class EventService {
  private readonly API = `${environment.apiUrl}/events`;

  constructor(private http: HttpClient) {}

  getAll(page = 0, size = 10, sortBy = 'startDate', sortDir: 'asc' | 'desc' = 'asc'): Observable<PagedResponse<EventItem>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sortBy', sortBy)
      .set('sortDir', sortDir);
    return this.http.get<PagedResponse<EventItem>>(this.API, { params });
  }

  search(q: string, page = 0, size = 10): Observable<PagedResponse<EventItem>> {
    const params = new HttpParams().set('q', q).set('page', page).set('size', size);
    return this.http.get<PagedResponse<EventItem>>(`${this.API}/search`, { params });
  }

  getUpcoming(): Observable<EventItem[]> {
    return this.http.get<EventItem[]>(`${this.API}/upcoming`);
  }

  getOngoing(): Observable<EventItem[]> {
    return this.http.get<EventItem[]>(`${this.API}/ongoing`);
  }

  getById(id: number): Observable<EventItem> {
    return this.http.get<EventItem>(`${this.API}/${id}`);
  }

  create(payload: Partial<EventItem>): Observable<any> {
    return this.http.post(this.API, payload);
  }

  update(id: number, payload: Partial<EventItem>): Observable<any> {
    return this.http.put(`${this.API}/${id}`, payload);
  }

  remove(id: number): Observable<any> {
    return this.http.delete(`${this.API}/${id}`);
  }

  updateStatus(id: number, status: string): Observable<any> {
    const params = new HttpParams().set('status', status);
    return this.http.patch(`${this.API}/${id}/status`, null, { params });
  }

  register(id: number): Observable<any> {
    return this.http.post(`${this.API}/${id}/register`, null);
  }

  unregister(id: number): Observable<any> {
    return this.http.delete(`${this.API}/${id}/unregister`);
  }
}

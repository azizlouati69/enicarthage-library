import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface BorrowingItem {
  id: number;
  bookId: number;
  userId: number;
  borrowDate: string;
  returnDate?: string;
  status: 'ACTIVE' | 'OVERDUE' | 'RETURNED';
  fineAmount?: number;
}

export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

@Injectable({ providedIn: 'root' })
export class BorrowingService {
  private readonly API = `${environment.apiUrl}/borrowings`;

  constructor(private http: HttpClient) {}

  getAll(page = 0, size = 10, sortBy = 'borrowDate', sortDir: 'asc' | 'desc' = 'desc'): Observable<PagedResponse<BorrowingItem>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sortBy', sortBy)
      .set('sortDir', sortDir);
    return this.http.get<PagedResponse<BorrowingItem>>(this.API, { params });
  }

  getByUser(userId: number): Observable<BorrowingItem[]> {
    return this.http.get<BorrowingItem[]>(`${this.API}/user/${userId}`);
  }

  getMyBorrowings(): Observable<BorrowingItem[]> {
    return this.http.get<BorrowingItem[]>(`${this.API}/my-borrowings`);
  }

  getOverdue(): Observable<BorrowingItem[]> {
    return this.http.get<BorrowingItem[]>(`${this.API}/overdue`);
  }

  getActive(): Observable<BorrowingItem[]> {
    return this.http.get<BorrowingItem[]>(`${this.API}/active`);
  }

  borrow(bookId: number, userId: number): Observable<any> {
    const params = new HttpParams().set('bookId', bookId).set('userId', userId);
    return this.http.post(`${this.API}/borrow`, null, { params });
  }

  returnBook(borrowingId: number): Observable<any> {
    const params = new HttpParams().set('borrowingId', borrowingId);
    return this.http.post(`${this.API}/return`, null, { params });
  }

  extendDueDate(id: number, days: number): Observable<any> {
    const params = new HttpParams().set('days', days);
    return this.http.patch(`${this.API}/${id}/extend`, null, { params });
  }

  updateFine(id: number, fineAmount: number): Observable<any> {
    const params = new HttpParams().set('fineAmount', fineAmount);
    return this.http.patch(`${this.API}/${id}/fine`, null, { params });
  }
}

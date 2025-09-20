import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Book, BookCategory, BookSearchParams, BookStatus } from '../models/book.model';

export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number; // current page index
  size: number;   // page size
}

@Injectable({ providedIn: 'root' })
export class BookService {
  private readonly API = `${environment.apiUrl}/books`;

  constructor(private http: HttpClient) {}

  getAll(params?: Partial<BookSearchParams>): Observable<PagedResponse<Book>> {
    let httpParams = new HttpParams();

    if (params) {
      if (params.page !== undefined) httpParams = httpParams.set('page', params.page.toString());
      if (params.size !== undefined) httpParams = httpParams.set('size', params.size.toString());
      if (params.sortBy) httpParams = httpParams.set('sortBy', params.sortBy);
      if (params.sortDir) httpParams = httpParams.set('sortDir', params.sortDir);
    }

    return this.http.get<PagedResponse<Book>>(this.API, { params: httpParams });
  }

  search(q: string, params?: Partial<BookSearchParams>): Observable<PagedResponse<Book>> {
    let httpParams = new HttpParams().set('q', q);
    if (params) {
      if (params.page !== undefined) httpParams = httpParams.set('page', params.page.toString());
      if (params.size !== undefined) httpParams = httpParams.set('size', params.size.toString());
    }
    return this.http.get<PagedResponse<Book>>(`${this.API}/search`, { params: httpParams });
  }

  getAvailable(): Observable<Book[]> {
    return this.http.get<Book[]>(`${this.API}/available`);
  }

  getByCategory(category: BookCategory): Observable<Book[]> {
    return this.http.get<Book[]>(`${this.API}/category/${category}`);
  }

  getById(id: number): Observable<Book> {
    return this.http.get<Book>(`${this.API}/${id}`);
  }

  updateStatus(id: number, status: BookStatus): Observable<Book> {
    return this.http.patch<Book>(`${this.API}/${id}/status`, null, {
      params: new HttpParams().set('status', status)
    });
  }
}

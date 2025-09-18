export interface Book {
  id: number;
  title: string;
  author: string;
  isbn?: string;
  publisher: string;
  publicationYear: number;
  category: BookCategory;
  status: BookStatus;
  description?: string;
  coverImageUrl?: string;
  totalCopies: number;
  availableCopies: number;
  shelfLocation?: string;
  language?: string;
  pages?: number;
  price?: number;
  createdAt: string;
  updatedAt: string;
}

export enum BookCategory {
  FICTION = 'FICTION',
  NON_FICTION = 'NON_FICTION',
  SCIENCE = 'SCIENCE',
  TECHNOLOGY = 'TECHNOLOGY',
  HISTORY = 'HISTORY',
  LITERATURE = 'LITERATURE',
  PHILOSOPHY = 'PHILOSOPHY',
  MATHEMATICS = 'MATHEMATICS',
  PHYSICS = 'PHYSICS',
  CHEMISTRY = 'CHEMISTRY',
  BIOLOGY = 'BIOLOGY',
  MEDICINE = 'MEDICINE',
  ENGINEERING = 'ENGINEERING',
  BUSINESS = 'BUSINESS',
  ECONOMICS = 'ECONOMICS',
  POLITICS = 'POLITICS',
  SOCIOLOGY = 'SOCIOLOGY',
  PSYCHOLOGY = 'PSYCHOLOGY',
  ART = 'ART',
  MUSIC = 'MUSIC',
  SPORTS = 'SPORTS',
  TRAVEL = 'TRAVEL',
  COOKING = 'COOKING',
  REFERENCE = 'REFERENCE',
  TEXTBOOK = 'TEXTBOOK'
}

export enum BookStatus {
  AVAILABLE = 'AVAILABLE',
  BORROWED = 'BORROWED',
  RESERVED = 'RESERVED',
  MAINTENANCE = 'MAINTENANCE',
  LOST = 'LOST',
  DAMAGED = 'DAMAGED'
}

export interface BookSearchParams {
  q?: string;
  category?: BookCategory;
  author?: string;
  year?: number;
  status?: BookStatus;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: 'asc' | 'desc';
}
export interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: UserRole;
  status: UserStatus;
  studentId?: string;
  phoneNumber?: string;
  createdAt: string;
  lastLogin?: string;
}

export enum UserRole {
  ADMIN = 'ADMIN',
  LIBRARIAN = 'LIBRARIAN',
  STUDENT = 'STUDENT',
  FACULTY = 'FACULTY'
}

export enum UserStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  LOCKED = 'LOCKED',
  SUSPENDED = 'SUSPENDED'
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  studentId?: string;
  phoneNumber?: string;
}

export interface LoginResponse {
  token: string;
  type: string;
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: UserRole;
}
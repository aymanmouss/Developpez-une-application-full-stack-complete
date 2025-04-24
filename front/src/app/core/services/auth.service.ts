import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import {
  LoginRequest,
  RegisterRequest,
  User,
  UserDetails,
} from '../../models/auth';
import { ApiService } from './api.service';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<User | UserDetails | null>(
    null
  );
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private apiService: ApiService, private route: Router) {
    const token = localStorage.getItem('userToken');
    if (token) {
      this.fetchUserDetails().subscribe();
    }
  }
  login(credentials: LoginRequest): Observable<User> {
    return this.apiService.post<User>('auth/login', credentials).pipe(
      tap((user) => {
        localStorage.setItem('userToken', user.token);
        this.currentUserSubject.next(user);
      })
    );
  }
  register(credentials: RegisterRequest): Observable<User> {
    return this.apiService.post<User>('auth/register', credentials).pipe(
      tap((user) => {
        localStorage.setItem('userToken', JSON.stringify(user.token));
        this.currentUserSubject.next(user);
      })
    );
  }
  private fetchUserDetails(): Observable<UserDetails> {
    return this.apiService.get<UserDetails>(`'auth/me`).pipe(
      tap((user) => {
        this.currentUserSubject.next(user);
      })
    );
  }

  logout(): void {
    localStorage.removeItem('userToken');
    this.route.navigate(['/login']);
    this.currentUserSubject.next(null);
  }
  isLoggedIn(): boolean {
    return (
      !!localStorage.getItem('userToken') &&
      !this.isTokenExpired(localStorage.getItem('userToken')!)
    );
  }
  getToken(): string | null {
    return localStorage.getItem('userToken');
  }

  isTokenExpired(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.exp < Date.now() / 1000;
    } catch (error) {
      return true;
    }
  }
}

import { HttpClient, HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, filter, Observable, switchMap, take, throwError } from 'rxjs';
import { AuthService } from '../services/auth/auth.service';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthorizationInterceptorService implements HttpInterceptor {

  private isRefreshing: boolean = false;
  private refreshTokenSubject: BehaviorSubject<string | null> = new BehaviorSubject<string | null>(null);

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    let request = req;
    let token = this.authService.getAuthToken();
    
    if (token)
      request = this.addTokenHeader(req, token);
    

    return next.handle(request).pipe(
      catchError((err) => {
        if (err instanceof HttpErrorResponse && err.status == 401) {
          return this.handle401Error(request, next);
        }
        return throwError(() => err);
      }))
  }

  private addTokenHeader(request: HttpRequest<any>, token: string) {
    return request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  private handle401Error(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshTokenSubject.next(null);

      return this.authService.updateToken().pipe(
        switchMap((tokenResp: any) => {
            this.isRefreshing = false;
            this.refreshTokenSubject.next(tokenResp.token);
            this.authService.saveJwtToken(tokenResp);
            return next.handle(this.addTokenHeader(request, tokenResp.token));
        }),
        catchError((err) => {
            this.isRefreshing = false;
            this.router.navigate(['login']);
            return throwError(() => err); 
        })
    );
    }
    else {
      return this.refreshTokenSubject.pipe(
        filter((token: any) => token !== null),
        take(1),
        switchMap((token) => next.handle(this.addTokenHeader(request, token!)))
      );
    }
  }

}

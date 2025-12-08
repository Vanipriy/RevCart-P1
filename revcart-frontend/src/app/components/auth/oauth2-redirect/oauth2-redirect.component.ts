import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-oauth2-redirect',
  standalone: true,
  template: '<div style="text-align: center; padding: 50px;">Processing login...</div>'
})
export class OAuth2RedirectComponent implements OnInit {
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private http: HttpClient
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const token = params['token'];
      const error = params['error'];
      
      if (token) {
        const userData = {
          id: parseInt(params['id']),
          name: params['name'],
          email: params['email'],
          role: params['role']
        };
        
        localStorage.setItem('token', token);
        localStorage.setItem('user', JSON.stringify(userData));
        
        console.log('OAuth login successful:', userData);
        
        // Force reload to update the auth state
        window.location.href = '/';
      } else if (error) {
        console.error('OAuth error:', error);
        this.router.navigate(['/login']);
      } else {
        this.router.navigate(['/login']);
      }
    });
  }
}

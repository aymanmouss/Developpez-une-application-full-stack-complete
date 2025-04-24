import { Component, Input } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { Location } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { MatDrawer, MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';

@Component({
  selector: 'app-header',
  imports: [
    MatIconModule,
    MatDividerModule,
    RouterModule,
    MatSidenavModule,
    MatListModule,
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss',
})
export class HeaderComponent {
  @Input() drawer!: MatDrawer;

  constructor(
    private location: Location,
    public router: Router,
    private authService: AuthService
  ) {}

  goBack(): void {
    this.location.back();
  }
  navigateTo(route: string): void {
    this.router.navigate([route]);
  }
  isLogeedin(): boolean {
    return this.authService.isLoggedIn();
  }

  logOut(): void {
    this.authService.logout();
  }
  openMobileMenu(): void {
    this.drawer.open();
  }
}

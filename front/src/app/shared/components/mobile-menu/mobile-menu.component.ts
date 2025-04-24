import { Component, Input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatDrawer } from '@angular/material/sidenav';
import { AuthService } from '../../../core/services/auth.service';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-mobile-menu',
  imports: [MatListModule, MatIconModule, RouterModule],
  templateUrl: './mobile-menu.component.html',
  styleUrl: './mobile-menu.component.scss',
})
export class MobileMenuComponent {
  @Input() drawer!: MatDrawer;

  constructor(private authService: AuthService, private router: Router) {}
  logOut(): void {
    this.authService.logout();
    this.drawer.close();
  }

  navigateTo(route: string): void {
    this.router.navigate([route]);
  }
}

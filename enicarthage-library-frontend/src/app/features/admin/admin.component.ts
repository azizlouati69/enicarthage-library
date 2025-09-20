import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="card">
      <h2>Admin Panel</h2>
      <p>Placeholder component. Admin tools will be restored dynamically next.</p>
    </div>
  `
})
export class AdminComponent {}

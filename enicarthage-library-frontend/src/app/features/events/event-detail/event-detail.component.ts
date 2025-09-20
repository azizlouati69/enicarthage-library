import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-event-detail',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="card">
      <h2>Event Detail</h2>
      <p>Event ID: {{ id }}</p>
      <p>Placeholder component. Detailed view will be restored next.</p>
    </div>
  `
})
export class EventDetailComponent implements OnInit {
  id!: number;
  constructor(private route: ActivatedRoute) {}
  ngOnInit(): void { this.id = Number(this.route.snapshot.paramMap.get('id')); }
}

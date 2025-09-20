import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-book-detail',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="card">
      <h2>Book Detail</h2>
      <p>Book ID: {{ id }}</p>
      <p>This route is a placeholder. Detail view will be restored next.</p>
    </div>
  `
})
export class BookDetailComponent implements OnInit {
  id!: number;
  constructor(private route: ActivatedRoute) {}
  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
  }
}

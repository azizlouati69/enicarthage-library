import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { RouterModule } from '@angular/router';
import { Chart, ChartConfiguration, ChartType, registerables } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';
import { DashboardService, DashboardOverview } from '../../core/services/dashboard.service';
import { BookService, PagedResponse } from '../../core/services/book.service';
import { Book, BookStatus } from '../../core/models/book.model';

Chart.register(...registerables);

interface StatCard {
  title: string;
  value: string;
  icon: string;
  color: string;
  change: string;
  changeType: 'positive' | 'negative' | 'neutral';
}

interface RecentActivity {
  id: number;
  type: 'borrow' | 'return' | 'reserve' | 'event';
  title: string;
  description: string;
  timestamp: string;
  status: 'success' | 'warning' | 'info';
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatGridListModule,
    MatChipsModule,
    MatProgressBarModule,
    RouterModule,
    BaseChartDirective
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  statCards: StatCard[] = [];
  recentBooks: Book[] = [];

  recentActivities: RecentActivity[] = [
    {
      id: 1,
      type: 'borrow',
      title: 'Book Borrowed',
      description: 'John Doe borrowed "Introduction to Algorithms"',
      timestamp: '2 hours ago',
      status: 'success'
    },
    {
      id: 2,
      type: 'event',
      title: 'New Event',
      description: 'Book Launch: "Machine Learning Fundamentals"',
      timestamp: '4 hours ago',
      status: 'info'
    },
    {
      id: 3,
      type: 'return',
      title: 'Book Returned',
      description: 'Sarah Wilson returned "Data Structures"',
      timestamp: '6 hours ago',
      status: 'success'
    },
    {
      id: 4,
      type: 'reserve',
      title: 'Book Reserved',
      description: 'Mike Johnson reserved "Clean Code"',
      timestamp: '8 hours ago',
      status: 'warning'
    }
  ];

  // Chart configurations
  borrowingChartData: ChartConfiguration['data'] = {
    labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
    datasets: [
      {
        data: [65, 59, 80, 81, 56, 55],
        label: 'Borrowings',
        backgroundColor: 'rgba(63, 81, 181, 0.2)',
        borderColor: 'rgba(63, 81, 181, 1)',
        borderWidth: 2,
        fill: true
      },
      {
        data: [28, 48, 40, 19, 86, 27],
        label: 'Returns',
        backgroundColor: 'rgba(76, 175, 80, 0.2)',
        borderColor: 'rgba(76, 175, 80, 1)',
        borderWidth: 2,
        fill: true
      }
    ]
  };

  borrowingChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'top',
      },
      title: {
        display: true,
        text: 'Monthly Borrowing Trends'
      }
    },
    scales: {
      y: {
        beginAtZero: true
      }
    }
  };

  categoryChartData: ChartConfiguration['data'] = {
    labels: ['Fiction', 'Science', 'Technology', 'History', 'Literature', 'Other'],
    datasets: [{
      data: [25, 20, 18, 15, 12, 10],
      backgroundColor: [
        '#3f51b5',
        '#4caf50',
        '#ff9800',
        '#e91e63',
        '#9c27b0',
        '#607d8b'
      ]
    }]
  };

  categoryChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'bottom',
      },
      title: {
        display: true,
        text: 'Books by Category'
      }
    }
  };

  constructor(private dashboardService: DashboardService, private bookService: BookService) {}

  ngOnInit() {
    this.dashboardService.getOverview().subscribe((ov: DashboardOverview) => {
      const totalBooks = ov.books?.totalBooks ?? 0;
      const totalUsers = ov.users?.totalUsers ?? 0;
      const upcomingEvents = (ov.events as any)?.upcoming ?? 0;
      const activeBorrowings = (ov.borrowings as any)?.active ?? 0;

      this.statCards = [
        { title: 'Total Books', value: String(totalBooks), icon: 'library_books', color: '#3f51b5', change: '+5.2%', changeType: 'positive' },
        { title: 'Active Borrowings', value: String(activeBorrowings), icon: 'book_online', color: '#4caf50', change: '+12.1%', changeType: 'positive' },
        { title: 'Registered Users', value: String(totalUsers), icon: 'people', color: '#ff9800', change: '+8.7%', changeType: 'positive' },
        { title: 'Upcoming Events', value: String(upcomingEvents), icon: 'event', color: '#e91e63', change: '+3', changeType: 'positive' }
      ];
    });

    this.bookService.getAll({ page: 0, size: 12 }).subscribe((resp: PagedResponse<Book>) => {
      this.recentBooks = resp.content;
    });
  }

  getBookStatusClass(status: BookStatus | string | undefined): string {
    switch (status) {
      case 'AVAILABLE': return 'available';
      case 'BORROWED': return 'borrowed';
      case 'RESERVED': return 'reserved';
      default: return 'available';
    }
  }

  getActivityIcon(type: string): string {
    const icons: { [key: string]: string } = {
      'borrow': 'book_online',
      'return': 'book',
      'reserve': 'bookmark',
      'event': 'event'
    };
    return icons[type] || 'info';
  }

  getActivityColor(status: string): string {
    const colors: { [key: string]: string } = {
      'success': '#4caf50',
      'warning': '#ff9800',
      'info': '#2196f3'
    };
    return colors[status] || '#757575';
  }
}
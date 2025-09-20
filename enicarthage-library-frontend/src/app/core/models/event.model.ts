export interface Event {
  id: number;
  title: string;
  description: string;
  startDate: string;
  endDate: string;
  location?: string;
  type: EventType;
  status: EventStatus;
  maxAttendees?: number;
  currentAttendees: number;
  imageUrl?: string;
  registrationRequired: boolean;
  registrationDeadline?: string;
  contactInfo?: string;
  createdAt: string;
  updatedAt: string;
  createdBy?: any;
}

export enum EventType {
  WORKSHOP = 'WORKSHOP',
  SEMINAR = 'SEMINAR',
  BOOK_LAUNCH = 'BOOK_LAUNCH',
  AUTHOR_TALK = 'AUTHOR_TALK',
  EXHIBITION = 'EXHIBITION',
  CONFERENCE = 'CONFERENCE',
  TRAINING = 'TRAINING',
  MEETING = 'MEETING',
  SOCIAL_EVENT = 'SOCIAL_EVENT',
  OTHER = 'OTHER'
}

export enum EventStatus {
  UPCOMING = 'UPCOMING',
  ONGOING = 'ONGOING',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED',
  POSTPONED = 'POSTPONED'
}
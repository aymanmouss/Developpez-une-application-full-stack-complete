import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatGridListModule } from '@angular/material/grid-list';
import { Topic } from '../../../models/topic';
import { SubscriptionService } from '../../../core/services/subscription.service';

@Component({
  selector: 'app-topic-card',
  imports: [MatCardModule, MatGridListModule, CommonModule],
  templateUrl: './topic-card.component.html',
  styleUrl: './topic-card.component.scss',
})
export class TopicCardComponent {
  @Input() topic!: Topic;
  @Input() textButton!: string;
  @Input() isSubscribed!: boolean;
  @Input() topicId!: number;

  constructor(private subscriptionService: SubscriptionService) {}

  subscribe(topicId: number): void {
    this.subscriptionService.getSubscription({ topicId: topicId }).subscribe({
      next: (data) => {
        console.log('Subscription successful:', data);
      },
      error: (err) => {
        console.error('Error subscribing to topic:', err);
      },
    });
  }
}

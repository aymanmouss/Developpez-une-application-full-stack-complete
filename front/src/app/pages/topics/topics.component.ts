import { Component, OnInit } from '@angular/core';
import { TopicCardComponent } from '../../shared/components/topic-card/topic-card.component';
import { Topic, topicResponse } from '../../models/topic';
import { Router } from '@angular/router';
import { TopicsService } from '../../core/services/topics.service';
import { SubscriptionService } from '../../core/services/subscription.service';

@Component({
  selector: 'app-topics',
  imports: [TopicCardComponent],
  templateUrl: './topics.component.html',
  styleUrl: './topics.component.scss',
})
export class TopicsComponent implements OnInit {
  constructor(private router: Router, private topicService: TopicsService) {}
  topics: topicResponse[] = [];

  ngOnInit(): void {
    this.topicService.getTopics().subscribe({
      next: (data) => {
        this.topics = data;
      },
      error: (err) => {
        console.error('Error fetching topics:', err);
      },
    });
  }

  navigateToTopic(): void {
    this.router.navigate(['/topics/create']);
  }

  textButton: string = 'S’abonner';
}

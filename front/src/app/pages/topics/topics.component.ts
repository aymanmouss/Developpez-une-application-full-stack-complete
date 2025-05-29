import { Component, OnInit } from "@angular/core";
import { TopicCardComponent } from "../../shared/components/topic-card/topic-card.component";
import { Topic, topicResponse } from "../../models/topic";
import { ActivatedRoute, Router } from "@angular/router";
import { TopicsService } from "../../core/services/topics.service";
import { SubscriptionService } from "../../core/services/subscription.service";
import { CommonModule } from "@angular/common";
import { MatButtonModule } from "@angular/material/button";
import { BehaviorSubject } from "rxjs";

@Component({
  selector: "app-topics",
  standalone: true,
  imports: [TopicCardComponent, CommonModule, MatButtonModule],
  templateUrl: "./topics.component.html",
  styleUrl: "./topics.component.scss",
})
export class TopicsComponent implements OnInit {
  constructor(
    private router: Router,
    private topicService: TopicsService,
    private route: ActivatedRoute,
    private subscriptionService: SubscriptionService
  ) {}

  topics: topicResponse[] = [];

  private subscribedTopicsSubject = new BehaviorSubject<Set<number>>(
    new Set<number>()
  );

  ngOnInit(): void {
    this.topicService.getTopics().subscribe({
      next: (data) => {
        this.topics = data;
      },
      error: (err) => {
        console.error("Error fetching topics:", err);
      },
    });

    this.route.data.subscribe((data) => {
      const subscribedIds = new Set<number>(data["subscribtion"] as number[]);
      this.subscribedTopicsSubject.next(subscribedIds);
    });
  }

  navigateToTopic(): void {
    this.router.navigate(["/topics/create"]);
  }

  isSubscribe(topicId: number): boolean {
    return this.subscribedTopicsSubject.value.has(topicId);
  }

  subscribse = (topicId: number): void => {
    console.log(this.isSubscribe(topicId));
    if (!this.isSubscribe(topicId)) {
      this.subscriptionService.getSubscription({ topicId: topicId }).subscribe({
        next: (data) => {
          const updatedSubscribedTopics = new Set(
            this.subscribedTopicsSubject.value
          );
          updatedSubscribedTopics.add(topicId);
          this.subscribedTopicsSubject.next(updatedSubscribedTopics);
        },
        error: (err) => {
          console.error("Error subscribing to topic:", err);
        },
      });
    }
  };
}

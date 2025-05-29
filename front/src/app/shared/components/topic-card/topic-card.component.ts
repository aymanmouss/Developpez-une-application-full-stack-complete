import { CommonModule } from "@angular/common";
import { Component, Input } from "@angular/core";
import { MatCardModule } from "@angular/material/card";
import { MatGridListModule } from "@angular/material/grid-list";
import { Topic } from "../../../models/topic";
import { SubscriptionService } from "../../../core/services/subscription.service";

@Component({
  selector: "app-topic-card",
  imports: [MatCardModule, MatGridListModule, CommonModule],
  templateUrl: "./topic-card.component.html",
  styleUrl: "./topic-card.component.scss",
})
export class TopicCardComponent {
  @Input() topic!: Topic;
  @Input() textButton!: string;
  @Input() isSubscribed: boolean = false;
  @Input() topicId!: number;
  @Input() isDisabled: string = "";
  @Input() subscribeFn!: (topicId: number) => void;
  @Input() disable: String = "subscribe-button-color";

  constructor(private subscriptionService: SubscriptionService) {}
}

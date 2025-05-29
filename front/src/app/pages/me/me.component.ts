import { Component, OnInit } from "@angular/core";
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatInputModule } from "@angular/material/input";
import { AuthService } from "../../core/services/auth.service";
import { MatDividerModule } from "@angular/material/divider";
import { Topic } from "../../models/topic";
import { TopicCardComponent } from "../../shared/components/topic-card/topic-card.component";
import { UserDetails } from "../../models/auth";
import { TopicsService } from "../../core/services/topics.service";
import { SubscriptionService } from "../../core/services/subscription.service";
import { BehaviorSubject } from "rxjs";
import { NgForOf, CommonModule } from "@angular/common";
import { ChangeDetectorRef } from "@angular/core";

@Component({
  selector: "app-me",
  standalone: true,
  imports: [
    CommonModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    ReactiveFormsModule,
    MatDividerModule,
    TopicCardComponent,
  ],
  templateUrl: "./me.component.html",
  styleUrl: "./me.component.scss",
})
export class MeComponent implements OnInit {
  responseForm: FormGroup;
  userdetails!: UserDetails;

  private topicsSubject = new BehaviorSubject<Topic[]>([]);
  topics$ = this.topicsSubject.asObservable();
  private subscribedTopicsSubject = new BehaviorSubject<Set<number>>(
    new Set<number>()
  );

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private topicService: TopicsService,
    private subscriptionService: SubscriptionService,
    private cdr: ChangeDetectorRef
  ) {
    this.responseForm = this.fb.group({
      username: ["", Validators.required],
      email: ["", Validators.required],
      password: [""],
    });
  }

  ngOnInit(): void {
    this.authService.fetchUserDetails().subscribe((user) => {
      this.userdetails = user;
      this.responseForm.patchValue({
        username: this.userdetails.username,
        email: this.userdetails.email,
      });
    });

    this.topicService.getTopicByUserId().subscribe({
      next: (topics) => {
        this.topicsSubject.next(topics);
      },
      error: (err) => {
        console.error("Error fetching topics:", err);
      },
    });
  }
  isSubscribe(topicId: number): boolean {
    return this.subscribedTopicsSubject.value.has(topicId);
  }
  updateUserDetails() {
    const formValue = this.responseForm.value;

    this.authService.updateUserDetails(formValue).subscribe({
      next: (response) => {
        console.log("User updated successfully!", response);
      },
      error: (err) => {
        console.error("Error updating user:", err);
      },
    });
  }

  unsubscribe = (topicId: number) => {
    if (!this.isSubscribe(topicId)) {
      this.subscriptionService.deleteSubscription({ topicId }).subscribe({
        next: () => {
          const updatedSubscribedTopics = new Set(
            this.subscribedTopicsSubject.value
          );
          updatedSubscribedTopics.add(topicId);
          this.subscribedTopicsSubject.next(updatedSubscribedTopics);
        },
        error: (err) => {
          console.error("Error unsubscribing from topic:", err);
        },
      });
    }
  };
}

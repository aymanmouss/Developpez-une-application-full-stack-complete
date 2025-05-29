import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { topicResponse } from "../../models/topic";
import { SubscriptionService } from "../services/subscription.service";

@Injectable({
  providedIn: "root",
})
export class SubscriptionResolver {
  constructor(private subscriptionService: SubscriptionService) {}
  resolve(): Observable<number[]> {
    return this.subscriptionService.getSubscriptions();
  }
}

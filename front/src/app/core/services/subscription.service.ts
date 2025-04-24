import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { subscriptionRequest } from '../../models/subscription';
import { Subscription } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class SubscriptionService {
  constructor(private apiService: ApiService) {}

  getSubscription(subscription: subscriptionRequest) {
    return this.apiService.post<Subscription>('subscriptions', subscription);
  }
  deleteSubscription(topicId: subscriptionRequest) {
    return this.apiService.delete<String>('subscriptions', topicId);
  }
}

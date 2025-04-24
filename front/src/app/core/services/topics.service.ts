import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { CreateTopicRequest, topicResponse } from '../../models/topic';

@Injectable({
  providedIn: 'root',
})
export class TopicsService {
  constructor(private apiService: ApiService) {}

  getTopics() {
    return this.apiService.get<topicResponse[]>('topics');
  }

  postTopic(topic: CreateTopicRequest) {
    return this.apiService.post<topicResponse>('topics', topic);
  }
}

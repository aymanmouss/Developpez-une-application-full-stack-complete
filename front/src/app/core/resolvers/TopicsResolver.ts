import { Observable } from 'rxjs';
import { TopicsService } from '../services/topics.service';
import { topicResponse } from '../../models/topic';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class TopicsResolver {
  constructor(private topicsService: TopicsService) {}
  resolve(): Observable<topicResponse[]> {
    return this.topicsService.getTopics();
  }
}

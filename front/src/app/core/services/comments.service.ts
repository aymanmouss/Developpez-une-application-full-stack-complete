import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Comment, CreateCommentRequest } from '../../models/comments';

@Injectable({
  providedIn: 'root',
})
export class CommentsService {
  constructor(private apiService: ApiService) {}

  getComments(postId: number) {
    return this.apiService.get<Comment[]>(`comments/${postId}`);
  }
  postComment(createCommentRequest: CreateCommentRequest) {
    return this.apiService.post<Comment>(
      'comments/comments',
      createCommentRequest
    );
  }
}

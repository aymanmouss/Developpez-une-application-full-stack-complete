import { Injectable, signal } from '@angular/core';
import { ApiService } from './api.service';
import { CreatePostRequest, Post } from '../../models/post';
import { BehaviorSubject, tap } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PostService {
  posts = signal<Post[]>([]);
  post = signal<Post | null>(null);

  constructor(private apiservice: ApiService) {}

  getPosts(): void {
    this.apiservice.get<Post[]>('posts').subscribe((posts) => {
      this.posts.set(posts);
    });
  }

  getPostById(postId: string | null) {
    return this.apiservice.get<Post>(`posts/${postId}`).subscribe((post) => {
      this.post.set(post);
    });
  }

  postPost(post: CreatePostRequest) {
    return this.apiservice
      .post<Post>('posts', post)
      .pipe(tap(() => this.getPosts()));
  }

  putPost(post: Post) {
    return this.apiservice.put<Post>(`posts/${post.id}`, post);
  }
}

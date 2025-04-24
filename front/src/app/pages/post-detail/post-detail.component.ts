import { CommonModule, Location, NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MatDivider } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { CommentComponent } from '../../shared/components/comment/comment.component';
import { Comment, CreateCommentRequest } from '../../models/comments';
import { MatFormFieldModule } from '@angular/material/form-field';
import { ActivatedRoute } from '@angular/router';
import { PostService } from '../../core/services/post.service';
import { MatInputModule } from '@angular/material/input';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { CommentsService } from '../../core/services/comments.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-post-detail',
  imports: [
    MatIconModule,
    MatInputModule,
    MatDivider,
    CommentComponent,
    MatFormFieldModule,
    CommonModule,
    ReactiveFormsModule,
  ],
  templateUrl: './post-detail.component.html',
  styleUrl: './post-detail.component.scss',
})
export class PostDetailComponent implements OnInit {
  responseForm: FormGroup;
  postId!: string | null;
  comments: Comment[] = [];

  constructor(
    private location: Location,
    private route: ActivatedRoute,
    public postService: PostService,
    private fb: FormBuilder,
    private commentService: CommentsService
  ) {
    this.responseForm = this.fb.group({
      comment: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    this.postId = this.route.snapshot.paramMap.get('id');
    this.postService.getPostById(this.postId);
    this.loadComments();
  }
  goBack(): void {
    this.location.back();
  }
  loadComments() {
    if (this.postId) {
      this.commentService.getComments(Number(this.postId)).subscribe({
        next: (response) => {
          this.comments = response;
          console.log('Comments loaded successfully:', this.comments);
        },
        error: (error) => {
          console.error('Error loading comments:', error);
        },
      });
    }
  }
  commentSubmit(): void {
    if (this.responseForm.valid && this.postId) {
      const payload: CreateCommentRequest = {
        postId: Number(this.postId),
        content: this.responseForm.value.comment,
      };

      this.commentService.postComment(payload).subscribe({
        next: (response) => {
          this.responseForm.reset();
          this.comments.push(response);
        },
        error: (error) => {
          console.error('Error posting comment:', error);
        },
      });
    }
    this.responseForm.reset();
  }
}

import { CommonModule, Location } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { ActivatedRoute, Router } from '@angular/router';
import { map, Observable } from 'rxjs';
import { topicResponse } from '../../models/topic';
import { TopicsService } from '../../core/services/topics.service';
import { Post } from '../../models/post';
import { PostService } from '../../core/services/post.service';

@Component({
  selector: 'app-create-post',
  imports: [
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    ReactiveFormsModule,
    MatIconModule,
    CommonModule,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './create-post.component.html',
  styleUrl: './create-post.component.scss',
})
export class CreatePostComponent implements OnInit {
  responseForm: FormGroup;
  topics$!: Observable<topicResponse[]>;
  post!: Post;
  constructor(
    private fb: FormBuilder,
    private location: Location,
    private router: Router,
    private route: ActivatedRoute,
    private postsService: PostService
  ) {
    this.responseForm = this.fb.group({
      topicId: ['', Validators.required],
      title: ['', Validators.required],
      content: ['', Validators.required],
    });
  }
  ngOnInit(): void {
    this.topics$ = this.route.data.pipe(map((data) => data['topics']));
  }
  goBack(): void {
    this.location.back();
  }
  onSubmit(): void {
    if (this.responseForm.valid) {
      this.postsService.postPost(this.responseForm.value).subscribe({
        next: (response) => {
          this.post = response;
        },
        error: (error) => {
          console.error('Error creating post:', error);
        },
      });
      this.router.navigate(['/posts'], {
        queryParams: { reload: new Date().getTime() },
      });
    } else {
      console.log('Form is invalid');
    }
  }
}

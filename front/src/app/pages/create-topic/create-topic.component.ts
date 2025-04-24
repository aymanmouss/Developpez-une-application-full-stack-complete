import { Location } from '@angular/common';
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
import { TopicsService } from '../../core/services/topics.service';
import { map, Observable } from 'rxjs';
import { topicResponse } from '../../models/topic';

@Component({
  selector: 'app-topic-post',
  imports: [
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    ReactiveFormsModule,
    MatIconModule,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './create-topic.component.html',
  styleUrl: './create-topic.component.scss',
})
export class CreateTopicComponent {
  responseForm: FormGroup;
  constructor(
    private fb: FormBuilder,
    private location: Location,
    private router: Router,
    private topicsService: TopicsService
  ) {
    this.responseForm = this.fb.group({
      title: ['', [Validators.required]],
      description: ['', [Validators.required]],
    });
  }

  goBack(): void {
    this.location.back();
  }
  onSubmit(): void {
    if (this.responseForm.valid) {
      this.topicsService.postTopic(this.responseForm.value).subscribe({
        next: (response) => {
          this.router.navigate(['/topics']);
        },
        error: (error) => {
          console.error('Error creating topic:', error);
        },
      });
      console.log(this.responseForm.value);
    }
  }
}

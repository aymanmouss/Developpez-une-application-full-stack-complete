import { I } from '@angular/cdk/keycodes';
import { Component, Input } from '@angular/core';
import { Comment } from '../../../models/comments';

@Component({
  selector: 'app-comment',
  imports: [],
  templateUrl: './comment.component.html',
  styleUrl: './comment.component.scss',
})
export class CommentComponent {
  @Input() comment!: Comment;
}

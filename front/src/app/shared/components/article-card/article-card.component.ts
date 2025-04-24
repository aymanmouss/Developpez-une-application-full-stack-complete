import { Component, Input } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatGridListModule } from '@angular/material/grid-list';
import { CommonModule } from '@angular/common';
import { Post } from '../../../models/post';

@Component({
  selector: 'app-article-card',
  imports: [MatCardModule, MatGridListModule, CommonModule],
  templateUrl: './article-card.component.html',
  styleUrl: './article-card.component.scss',
})
export class ArticleCardComponent {
  @Input() post!: Post;
}

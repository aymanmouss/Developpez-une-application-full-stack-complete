import { Component, OnInit } from "@angular/core";
import { Post } from "../../models/post";
import { ArticleCardComponent } from "../../shared/components/article-card/article-card.component";
import { MatGridListModule } from "@angular/material/grid-list";
import { CommonModule } from "@angular/common";
import { MatIconModule } from "@angular/material/icon";
import { ActivatedRoute, Router } from "@angular/router";
import { MatFormFieldModule } from "@angular/material/form-field";
import { map, Observable } from "rxjs";
import { PostService } from "../../core/services/post.service";

@Component({
  selector: "app-posts",
  imports: [
    ArticleCardComponent,
    MatGridListModule,
    CommonModule,
    MatIconModule,
    MatFormFieldModule,
  ],
  templateUrl: "./posts.component.html",
  styleUrl: "./posts.component.scss",
})
export class PostsComponent implements OnInit {
  columns: number = 2;
  ascending = true;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    public postService: PostService
  ) {}
  ngOnInit(): void {
    this.postService.getPosts();
  }

  navigateToPostCreation() {
    this.router.navigate(["/posts/create"]);
  }
  navigateToPostDetails(postId: number | undefined) {
    this.router.navigate(["/posts", postId]);
  }
  trackById(index: number, post: Post): number | undefined {
    return post?.id;
  }
  toggleSortOrder() {
    this.postService.posts().sort((a, b) => {
      return this.ascending
        ? a.title.localeCompare(b.title)
        : b.title.localeCompare(a.title);
    });
    this.ascending = !this.ascending;
  }
  toggleIcon() {
    return this.ascending ? "south" : "north";
  }
}

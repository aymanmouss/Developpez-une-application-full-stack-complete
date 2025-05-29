import { Routes } from "@angular/router";
import { HomeComponent } from "./pages/home/home.component";
import { LoginComponent } from "./pages/auth/login/login.component";
import { RegisterComponent } from "./pages/auth/register/register.component";
import { PostsComponent } from "./pages/posts/posts.component";
import { AuthGuard } from "./core/guards/auth.guard";
import { LoggedInAuthGuard } from "./core/guards/logged-in-auth.guard";
import { TopicsComponent } from "./pages/topics/topics.component";
import { MeComponent } from "./pages/me/me.component";
import { CreatePostComponent } from "./pages/create-post/create-post.component";
import { CreateTopicComponent } from "./pages/create-topic/create-topic.component";
import { PostDetailComponent } from "./pages/post-detail/post-detail.component";
import { TopicsResolver } from "./core/resolvers/TopicsResolver";
import { SubscriptionResolver } from "./core/resolvers/SubscriptionResolver";

export const routes: Routes = [
  { path: "", component: HomeComponent, canActivate: [LoggedInAuthGuard] },
  {
    path: "login",
    component: LoginComponent,
    canActivate: [LoggedInAuthGuard],
  },

  {
    path: "register",
    component: RegisterComponent,
    canActivate: [LoggedInAuthGuard],
  },
  {
    path: "posts",
    component: PostsComponent,
    canActivate: [AuthGuard],
    runGuardsAndResolvers: "always",
  },
  {
    path: "topics",
    component: TopicsComponent,
    canActivate: [AuthGuard],
    resolve: {
      subscribtion: SubscriptionResolver,
    },
  },
  { path: "me", component: MeComponent, canActivate: [AuthGuard] },
  {
    path: "posts/create",
    component: CreatePostComponent,
    canActivate: [AuthGuard],
    resolve: {
      topics: TopicsResolver,
    },
  },
  {
    path: "topics/create",
    component: CreateTopicComponent,
    canActivate: [AuthGuard],
  },
  {
    path: "posts/:id",
    component: PostDetailComponent,
    canActivate: [AuthGuard],
  },
];

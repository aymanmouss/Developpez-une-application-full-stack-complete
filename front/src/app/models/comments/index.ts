export interface Comment {
  id: number;
  postId: number;
  userId: number;
  username: string;
  content: string;
  createdAt: Date;
}

export interface CreateCommentRequest {
  postId: number;
  content: string;
}

export interface Post {
  id: number;
  authorId: number;
  username: string;
  topicId: number;
  topicName: string;
  title: string;
  content: string;
  createdAt: Date;
}

export interface CreatePostRequest {
  title: string;
  content: string;
  topicId: number;
}

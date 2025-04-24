export interface Topic {
  id: number;
  title: string;
  description: string;
}

export interface CreateTopicRequest {
  title: string;
  description: string;
}

export interface topicResponse {
  id: number;
  title: string;
  description: string;
}

package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.PostRequestDTO;
import com.openclassrooms.mddapi.dto.PostResponseDTO;
import com.openclassrooms.mddapi.mapper.PostMapper;
import com.openclassrooms.mddapi.model.Post;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.repository.TopicRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.rmi.server.ServerCloneException;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final AuthService securityService;
    private final PostMapper postMapper;
    private final TopicRepository topicRepository;

    public PostResponseDTO createPost(PostRequestDTO postRequestDTO) {
        try {
            User currentUser= securityService.getCurrentUser();
            Topic topic = topicRepository.findById(postRequestDTO.getTopicId())
                    .orElseThrow(() -> new ServiceException("Topic not found"));
            Post postEntity = postMapper.toEntity(postRequestDTO, currentUser, topic);
            postRepository.save(postEntity);
            return postMapper.toDto(postEntity);
        } catch (Exception e){
            throw new ServiceException("Error creating post", e);
        }
    }

    public List<PostResponseDTO> getAllPosts() {
        try {
            List<Post> posts = postRepository.findAll();
            return posts.stream()
                    .map(postMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e){
            throw new ServiceException("Error getting posts", e);
        }
    }

    public PostResponseDTO getPostById(Long id) {
        try {
            Post post = postRepository.findById(id)
                    .orElseThrow(() -> new ServiceException("Post not found"));
            return postMapper.toDto(post);
        } catch (Exception e){
            throw new ServiceException("Error getting post", e);
        }
    }
}

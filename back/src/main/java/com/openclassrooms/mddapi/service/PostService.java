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

/**
 * Service class responsible for managing posts.
 * Handles creating posts, retrieving all posts, and fetching individual posts by ID.
 */
@Getter
@Setter
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final AuthService securityService;
    private final PostMapper postMapper;
    private final TopicRepository topicRepository;

    /**
     * Creates a new post by the currently authenticated user, linked to a specific topic.
     *
     * @param postRequestDTO the data for the post to be created
     * @return the created post as a response DTO
     * @throws ServiceException if the topic is not found or the post cannot be saved
     */
    public PostResponseDTO createPost(PostRequestDTO postRequestDTO) {
        try {
            User currentUser = securityService.getCurrentUser();
            Topic topic = topicRepository.findById(postRequestDTO.getTopicId())
                    .orElseThrow(() -> new ServiceException("Topic not found"));
            Post postEntity = postMapper.toEntity(postRequestDTO, currentUser, topic);
            postRepository.save(postEntity);
            return postMapper.toDto(postEntity);
        } catch (Exception e) {
            throw new ServiceException("Error creating post", e);
        }
    }

    /**
     * Retrieves all posts from the database.
     *
     * @return a list of all posts as response DTOs
     * @throws ServiceException if retrieving posts fails
     */
    public List<PostResponseDTO> getAllPosts() {
        try {
            List<Post> posts = postRepository.findAll();
            return posts.stream()
                    .map(postMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ServiceException("Error getting posts", e);
        }
    }

    /**
     * Retrieves a specific post by its ID.
     *
     * @param id the ID of the post to retrieve
     * @return the corresponding post as a response DTO
     * @throws ServiceException if the post is not found or retrieval fails
     */
    public PostResponseDTO getPostById(Long id) {
        try {
            Post post = postRepository.findById(id)
                    .orElseThrow(() -> new ServiceException("Post not found"));
            return postMapper.toDto(post);
        } catch (Exception e) {
            throw new ServiceException("Error getting post", e);
        }
    }
}
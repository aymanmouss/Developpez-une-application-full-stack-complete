package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.TopicRequestDTO;
import com.openclassrooms.mddapi.dto.TopicResponseDTO;
import com.openclassrooms.mddapi.mapper.TopicMapper;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing topics.
 * Handles creating new topics, retrieving all topics,
 * and fetching topics subscribed to by the current user.
 */
@RequiredArgsConstructor
@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final TopicMapper topicMapper;

    /**
     * Retrieves all available topics in the system.
     *
     * @return a list of all topics as response DTOs
     * @throws ServiceException if an error occurs while fetching topics
     */
    public List<TopicResponseDTO> getAllTopics() {
        try {
            List<Topic> topics = topicRepository.findAll();
            return topics.stream()
                    .map(topicMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ServiceException("Error getting topics", e);
        }
    }

    /**
     * Creates a new topic from the provided data.
     *
     * @param dto the topic creation data
     * @return the created topic as a response DTO
     * @throws ServiceException if topic creation fails
     */
    public TopicResponseDTO createTopic(TopicRequestDTO dto) {
        try {
            Topic topicEntity = topicMapper.toEntity(dto);
            topicRepository.save(topicEntity);
            return topicMapper.toDTO(topicEntity);
        } catch (Exception e) {
            throw new ServiceException("Error creating topic", e);
        }
    }

    /**
     * Retrieves topics subscribed to by the currently authenticated user.
     *
     * @return a list of topics associated with the current user
     */
    public List<TopicResponseDTO> getTopicsByUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        List<Topic> topics = topicRepository.findTopicsByUserId(userId);
        return topics.stream()
                .map(topicMapper::toDTO)
                .collect(Collectors.toList());
    }
}
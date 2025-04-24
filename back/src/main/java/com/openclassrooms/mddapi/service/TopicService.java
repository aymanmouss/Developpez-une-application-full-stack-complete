package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.TopicRequestDTO;
import com.openclassrooms.mddapi.dto.TopicResponseDTO;
import com.openclassrooms.mddapi.mapper.TopicMapper;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TopicService {
    private final TopicRepository topicRepository;
    private final TopicMapper topicMapper;

    public List<TopicResponseDTO> getAllTopics() {
        try {
            List<Topic> topics = topicRepository.findAll();
            return topics.stream()
                    .map(topicMapper::toDTO)
                    .collect(Collectors.toList());

        }catch (Exception e){
            throw new ServiceException("Error getting topics", e);
        }
    }

    public TopicResponseDTO createTopic(TopicRequestDTO dto){
        try{
            Topic topicEntity = topicMapper.toEntity(dto);
            topicRepository.save(topicEntity);
            return topicMapper.toDTO(topicEntity);
        }catch (Exception e){
            throw new ServiceException("Error creating topic", e);
        }
    }
}

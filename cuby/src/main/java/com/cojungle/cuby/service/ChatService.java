package com.cojungle.cuby.controller.service;

import com.shopify.chatbot.dto.ChatMessageDTO;
import com.shopify.chatbot.dto.ChatSessionDTO;
import com.shopify.chatbot.entity.ChatMessage;
import com.shopify.chatbot.entity.ChatSession;
import com.shopify.chatbot.exception.ResourceNotFoundException;
import com.shopify.chatbot.repository.ChatMessageRepository;
import com.shopify.chatbot.repository.ChatSessionRepository;
import com.shopify.chatbot.mapper.ChatMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private final AIService aiService;
    private final ChatMapper chatMapper;
    private final ConfigService configService;

    @Autowired
    public ChatService(
            ChatSessionRepository sessionRepository,
            ChatMessageRepository messageRepository,
            AIService aiService,
            ChatMapper chatMapper,
            ConfigService configService) {
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        this.aiService = aiService;
        this.chatMapper = chatMapper;
        this.configService = configService;
    }

    /**
     * Create a new chat session
     */
    @Transactional
    public ChatSessionDTO createSession(String storeId) {
        // Create new session
        ChatSession session = new ChatSession();
        session.setStoreId(storeId);
        session.setCreatedAt(Instant.now());
        session.setLastActivity(Instant.now());

        // Save session
        ChatSession savedSession = sessionRepository.save(session);
        logger.info("Created new chat session: {}", savedSession.getId());

        // Add welcome message if configured
        String welcomeMessage = configService.getConfig(storeId).getWelcomeMessage();
        if (welcomeMessage != null && !welcomeMessage.trim().isEmpty()) {
            ChatMessage message = new ChatMessage();
            message.setSessionId(savedSession.getId());
            message.setContent(welcomeMessage);
            message.setSender("bot");
            message.setTimestamp(Instant.now());
            messageRepository.save(message);
        }

        return chatMapper.sessionToDTO(savedSession);
    }

    /**
     * Get a specific chat session
     */
    @Cacheable(value = "sessionCache", key = "#sessionId")
    public ChatSessionDTO getSession(UUID sessionId) {
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found: " + sessionId));

        return chatMapper.sessionToDTO(session);
    }

    /**
     * Get all sessions for a store
     */
    public List<ChatSessionDTO> getSessionsByStore(String storeId, int limit, int offset) {
        return sessionRepository.findByStoreIdOrderByLastActivityDesc(storeId)
                .stream()
                .skip(offset)
                .limit(limit)
                .map(chatMapper::sessionToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get chat history for a session
     */
    @Cacheable(value = "chatHistoryCache", key = "#sessionId")
    public List<ChatMessageDTO> getChatHistory(UUID sessionId) {
        // Verify session exists
        if (!sessionRepository.existsById(sessionId)) {
            throw new ResourceNotFoundException("Chat session not found: " + sessionId);
        }

        List<ChatMessage> messages = messageRepository.findBySessionIdOrderByTimestampAsc(sessionId);
        return messages.stream()
                .map(chatMapper::messageToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Process a user message and generate a response
     */
    @Transactional
    @CacheEvict(value = {"sessionCache", "chatHistoryCache"}, key = "#sessionId")
    public ChatMessageDTO processMessage(UUID sessionId, String userMessage, String storeId) {
        // Verify session exists
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found: " + sessionId));

        // Update session last activity
        session.setLastActivity(Instant.now());
        sessionRepository.save(session);

        // Save user message
        ChatMessage userMsg = new ChatMessage();
        userMsg.setSessionId(sessionId);
        userMsg.setContent(userMessage);
        userMsg.setSender("user");
        userMsg.setTimestamp(Instant.now());
        messageRepository.save(userMsg);

        // Get chat history
        List<ChatMessage> chatHistory = messageRepository.findBySessionIdOrderByTimestampAsc(sessionId);

        // Generate AI response
        ChatMessageDTO botResponseDTO = aiService.generateResponse(sessionId, userMessage, chatHistory, storeId);

        // Save bot response to database
        ChatMessage botMsg = chatMapper.dtoToMessage(botResponseDTO);
        botMsg.setSessionId(sessionId);
        botMsg.setTimestamp(Instant.now());
        messageRepository.save(botMsg);

        return botResponseDTO;
    }

    /**
     * Delete a chat session and its messages
     */
    @Transactional
    @CacheEvict(value = {"sessionCache", "chatHistoryCache"}, key = "#sessionId")
    public void deleteSession(UUID sessionId) {
        // Verify session exists
        if (!sessionRepository.existsById(sessionId)) {
            throw new ResourceNotFoundException("Chat session not found: " + sessionId);
        }

        // Delete all messages in the session
        messageRepository.deleteBySessionId(sessionId);

        // Delete the session
        sessionRepository.deleteById(sessionId);

        logger.info("Deleted chat session: {}", sessionId);
    }
}

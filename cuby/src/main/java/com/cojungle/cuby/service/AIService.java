package com.cojungle.cuby.controller.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.shopify.chatbot.dto.ChatConfigDTO;
import com.shopify.chatbot.dto.ChatMessageDTO;
import com.shopify.chatbot.entity.ChatMessage;
import com.shopify.chatbot.entity.StoreInfo;
import com.shopify.chatbot.exception.AIServiceException;
import com.shopify.chatbot.repository.StoreInfoRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AIService {

    private static final Logger logger = LoggerFactory.getLogger(AIService.class);

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;
    private final ConfigService configService;
    private final ShopifyService shopifyService;
    private final StoreInfoRepository storeInfoRepository;

    @Autowired
    public AIService(
            ChatClient chatClient,
            ObjectMapper objectMapper,
            ConfigService configService,
            ShopifyService shopifyService,
            StoreInfoRepository storeInfoRepository) {
        this.chatClient = chatClient;
        this.objectMapper = objectMapper;
        this.configService = configService;
        this.shopifyService = shopifyService;
        this.storeInfoRepository = storeInfoRepository;
    }

    /**
     * Processes a user message and generates an AI response
     *
     * @param sessionId Current chat session ID
     * @param userMessage The user's message text
     * @param chatHistory List of previous messages in the conversation
     * @param storeId Shopify store ID
     * @return The AI-generated response
     */
    public ChatMessageDTO generateResponse(
            UUID sessionId,
            String userMessage,
            List<ChatMessage> chatHistory,
            String storeId) {

        try {
            // Get store configuration
            ChatConfigDTO config = configService.getConfig(storeId);

            // Create messages list for prompt
            List<Message> messages = createMessagesList(userMessage, chatHistory, storeId, config);

            // Configure options based on store settings
            OpenAiChatOptions options = OpenAiChatOptions.builder()
                    .withModel(config.getAiAssistance().getModel() != null
                            ? config.getAiAssistance().getModel()
                            : "gpt-4o")
                    .withTemperature(config.getAiAssistance().getTemperature())
                    .withMaxTokens(config.getAiAssistance().getMaxTokens())
                    .build();

            // Create prompt with messages and options
            Prompt prompt = new Prompt(messages, options);

            // Get AI response
            ChatResponse response = chatClient.call(prompt);
            String content = response.getResult().getOutput().getContent();

            // Process tool calls if any (equivalent to function calls in GPT API)
            ObjectNode metadata = objectMapper.createObjectNode();
            if (response.getResult().getToolCalls() != null && !response.getResult().getToolCalls().isEmpty()) {
                processToolCalls(response.getResult().getToolCalls(), metadata, storeId);
            }

            // Create response DTO
            return ChatMessageDTO.builder()
                    .id(UUID.randomUUID().toString())
                    .sessionId(sessionId.toString())
                    .content(content)
                    .sender("bot")
                    .timestamp(System.currentTimeMillis())
                    .metadata(metadata.size() > 0 ? objectMapper.convertValue(metadata, java.util.Map.class) : null)
                    .build();

        } catch (Exception e) {
            logger.error("Error generating AI response: {}", e.getMessage(), e);
            throw new AIServiceException("Failed to generate AI response: " + e.getMessage());
        }
    }

    /**
     * Creates the message list for the ChatGPT API request
     */
    private List<Message> createMessagesList(
            String userMessage,
            List<ChatMessage> chatHistory,
            String storeId,
            ChatConfigDTO config) {

        List<Message> messages = new ArrayList<>();

        // Add system message with store-specific context
        messages.add(new SystemMessage(getSystemPrompt(storeId, config)));

        // Add chat history (limited to last 10 messages to keep context size reasonable)
        int startIdx = Math.max(0, chatHistory.size() - 10);
        for (int i = startIdx; i < chatHistory.size(); i++) {
            ChatMessage msg = chatHistory.get(i);
            if (msg.getSender().equals("user")) {
                messages.add(new UserMessage(msg.getContent()));
            } else {
                messages.add(new org.springframework.ai.chat.messages.AssistantMessage(msg.getContent()));
            }
        }

        // Add the current user message
        messages.add(new UserMessage(userMessage));

        return messages;
    }

    /**
     * Processes tool calls from the AI response
     */
    private void processToolCalls(List<Object> toolCalls, ObjectNode metadata, String storeId) {
        try {
            for (Object toolCall : toolCalls) {
                JsonNode toolCallNode = objectMapper.valueToTree(toolCall);
                String name = toolCallNode.get("name").asText();
                JsonNode argumentsNode = toolCallNode.get("arguments");

                switch (name) {
                    case "search_products":
                        String query = argumentsNode.get("query").asText();
                        JsonNode products = shopifyService.searchProducts(storeId, query);
                        metadata.put("action", "product_search");
                        metadata.set("products", products);
                        break;

                    case "get_product_details":
                        String productId = argumentsNode.get("product_id").asText();
                        JsonNode product = shopifyService.getProductDetails(storeId, productId);
                        metadata.put("action", "product_details");
                        metadata.set("product", product);
                        break;

                    case "check_order":
                        String orderNumber = argumentsNode.get("order_number").asText();
                        String email = argumentsNode.get("email").asText();
                        metadata.put("action", "order_lookup");
                        metadata.put("orderNumber", orderNumber);
                        metadata.put("email", email);
                        break;

                    case "redirect":
                        String url = argumentsNode.get("url").asText();
                        metadata.put("action", "redirect");
                        metadata.put("url", url);
                        break;

                    default:
                        logger.warn("Unknown tool call: {}", name);
                }
            }
        } catch (Exception e) {
            logger.error("Error processing tool calls: {}", e.getMessage(), e);
        }
    }

    /**
     * Builds a system prompt with store-specific information
     */
    private String getSystemPrompt(String storeId, ChatConfigDTO config) {
        StringBuilder prompt = new StringBuilder();

        // If custom system prompt is provided in config, use that
        if (config.getAiAssistance().getSystemPrompt() != null &&
                !config.getAiAssistance().getSystemPrompt().trim().isEmpty()) {
            prompt.append(config.getAiAssistance().getSystemPrompt());
        } else {
            // Use default prompt with store data
            Optional<StoreInfo> storeInfo = storeInfoRepository.findById(storeId);

            prompt.append("You are a helpful customer service assistant for ");

            if (storeInfo.isPresent()) {
                StoreInfo store = storeInfo.get();
                prompt.append(store.getName())
                        .append(", an online store at ")
                        .append(store.getDomain())
                        .append(".\n\n");

                // Add store policies if available
                if (store.getPolicies() != null) {
                    prompt.append("Store Policies:\n");

                    if (store.getPolicies().getShippingPolicy() != null) {
                        prompt.append("Shipping Policy: ")
                                .append(store.getPolicies().getShippingPolicy())
                                .append("\n");
                    }

                    if (store.getPolicies().getRefundPolicy() != null) {
                        prompt.append("Refund Policy: ")
                                .append(store.getPolicies().getRefundPolicy())
                                .append("\n");
                    }
                }

                prompt.append("\n");
            } else {
                prompt.append("an online Shopify store.\n\n");
            }

            // Add capabilities
            prompt.append("Your capabilities:\n");

            if (config.isProductRecommendations()) {
                prompt.append("- You can search for products and provide product recommendations\n");
            }

            if (config.isOrderLookup()) {
                prompt.append("- You can help customers track their orders\n");
            }

            prompt.append("- You can answer questions about products, shipping, returns, and store policies\n")
                    .append("- You are friendly, helpful, and concise\n")
                    .append("- If you don't know the answer, say so and offer to connect the customer with a human agent\n\n");

            // Add function calling instructions
            prompt.append("You have access to these tools:\n")
                    .append("1. search_products(query): Search for products in the store\n");

            if (config.isProductRecommendations()) {
                prompt.append("2. get_product_details(product_id): Get detailed information about a specific product\n");
            }

            if (config.isOrderLookup()) {
                prompt.append("3. check_order(order_number, email): Check status of a customer's order\n");
            }

            prompt.append("4. redirect(url): Redirect the customer to a specific page on the store\n\n")
                    .append("When appropriate, use these tools by responding with a tool call.");
        }

        return prompt.toString();
    }
}
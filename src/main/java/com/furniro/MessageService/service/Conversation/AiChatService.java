package com.furniro.MessageService.service.Conversation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.furniro.MessageService.database.entity.Conversation;
import com.furniro.MessageService.database.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatService {

    private final RestClient geminiRestClient;
    private final ConversationRepository conversationRepository;
    private final ObjectMapper objectMapper;

    @Value("${GEMINI_API_KEY:}")
    private String apiKey;

    @Value("${GEMINI_MODEL:gemini-2.5-flash}")
    private String modelName;

    @Value("${PRODUCT_SERVICE_URL:http://localhost:8083}")
    private String productServiceUrl;

    /**
     * Call the Gemini API to get a chat completion response.
     */
    public String getChatCompletion(String userMessage, Integer conversationId) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.warn("GEMINI_API_KEY is not configured. Falling back to default automated message.");
            return "Hello! I am the Furniro Assistant. (Please configure GEMINI_API_KEY in the environment to enable full AI responses).";
        }

        try {
            String catalogContext = fetchCatalogContext();
            
            // Build the system instructions combined with the user prompt
            String systemInstructions = "You are a professional luxury furniture assistant for Furniro. " +
                    "Below is our product catalog:\n" + catalogContext + "\n\n" +
                    "Guidelines:\n" +
                    "- Answer customer questions clearly and in a polite, helpful manner.\n" +
                    "- If the customer explicitly asks for human support, a real person, or customer service, " +
                    "or if you cannot answer their query after trying, please include the text '[HANDOVER]' anywhere in your response, " +
                    "and explain that you are handing them over to a live staff member.";

            // Construct Gemini request payload
            Map<String, Object> textPart = Map.of("text", systemInstructions + "\n\nUser Question: " + userMessage);
            Map<String, Object> parts = Map.of("parts", List.of(textPart));
            Map<String, Object> contents = Map.of("contents", List.of(parts));

            String uri = "/v1beta/models/" + modelName + ":generateContent?key=" + apiKey;

            log.info("Sending request to Gemini API model: {}", modelName);

            String jsonResponse = geminiRestClient.post()
                    .uri(uri)
                    .body(contents)
                    .retrieve()
                    .body(String.class);

            // Extract the generated text using Jackson
            Map<?, ?> responseMap = objectMapper.readValue(jsonResponse, Map.class);
            List<?> candidates = (List<?>) responseMap.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map<?, ?> candidate = (Map<?, ?>) candidates.get(0);
                Map<?, ?> content = (Map<?, ?>) candidate.get("content");
                if (content != null) {
                    List<?> partsList = (List<?>) content.get("parts");
                    if (partsList != null && !partsList.isEmpty()) {
                        Map<?, ?> part = (Map<?, ?>) partsList.get(0);
                        String text = (String) part.get("text");
                        if (text != null) {
                            return text;
                        }
                    }
                }
            }

            return "I apologize, but I received an empty response from my system. How else can I assist you?";
        } catch (Exception e) {
            log.error("Failed to generate response from Gemini API: {}", e.getMessage(), e);
            return "Hello! I am currently experiencing technical difficulties retrieving AI replies. How can I help you today?";
        }
    }

    /**
     * Injects the active catalog context.
     * Fetches real-time products from ProductService, falls back to static catalog if ProductService is down.
     */
    @SuppressWarnings("unchecked")
    private String fetchCatalogContext() {
        try {
            String url = productServiceUrl + "/products?page=0&size=50";
            RestClient productClient = RestClient.builder().baseUrl(productServiceUrl).build();
            Map<String, Object> response = productClient.get()
                    .uri("/products?page=0&size=50")
                    .retrieve()
                    .body(Map.class);

            if (response != null && response.get("data") != null) {
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                List<Map<String, Object>> content = (List<Map<String, Object>>) data.get("content");
                if (content != null && !content.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (Map<String, Object> product : content) {
                        sb.append("- ").append(product.get("name"))
                          .append(" (Price: $").append(product.get("basePrice")).append("): ")
                          .append(product.get("description")).append("\n");
                    }
                    return sb.toString();
                }
            }
        } catch (Exception e) {
            log.warn("Failed to fetch product catalog from ProductService: {}. Using fallback mock catalog context.", e.getMessage());
        }

        // Fallback robust catalog context
        return "- 🛋️ Syltherine Sofa (Price: $120.00): Stylish luxury 3-seater sofa made from high-density foam.\n" +
                "- 🪑 Leviosa Chair (Price: $250.00): Ergonomic luxury office/lounge chair.\n" +
                "- 🪵 Lolito Dining Table (Price: $450.00): Solid oak dining table fitting up to 8 people.\n" +
                "- 🛏️ Respira Luxury Bed (Price: $799.00): Premium memory foam bed frame and mattress.\n" +
                "- 🛋️ Grifo Sectional Couch (Price: $1500.00): Expandable luxury lounge seating arrangement.";
    }

    /**
     * Checks if the AI response requires a human handover, processes it, and updates the conversation state.
     */
    public boolean checkAndProcessHandover(String responseText, Integer conversationId) {
        if (responseText != null && responseText.contains("[HANDOVER]")) {
            log.info("Handover detected for conversation ID: {}. Routing to human staff.", conversationId);
            try {
                Optional<Conversation> opt = conversationRepository.findById(conversationId);
                if (opt.isPresent()) {
                    Conversation conv = opt.get();
                    // Set staffId to a human agent (e.g. 2, or 0 representing "Unassigned / human queue")
                    conv.setStaffId(2);
                    conversationRepository.save(conv);
                    return true;
                }
            } catch (Exception e) {
                log.error("Failed to update conversation handover staff ID in database: {}", e.getMessage());
            }
        }
        return false;
    }
}

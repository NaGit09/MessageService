# Furniro MessageService - Strategic Enhancement Plan

This document details the strategic plan and proposed features for the **Furniro MessageService** backend.

---

## 🔍 Codebase Overview & Current Architecture

The **MessageService** is a Spring Boot application designed with a microservices-friendly, event-driven architecture. It integrates multiple enterprise-grade technologies:

*   **Real-time Communication**: Implements **STOMP over WebSocket (with SockJS support)** to handle instant bi-directional client-server messages.
*   **Message Broker**: Integrates **Apache Kafka** to consume cross-service events asynchronously (e.g., account activation, OTP requests, and notification triggers).
*   **Caching & Session Management**: Utilizes **Spring Session Data Redis** for fast distributed sessions and distributed cache access.
*   **Relational Persistence**: Uses **MySQL** along with **Spring Data JPA** for data persistence (e.g., conversations, message histories, subscriptions, and promotions).
*   **Marketing & Alert Channels**: Integrates **Spring Mail** to send dynamic transactional emails (user registration links, OTPs) and marketing blasts (promotions, newsletters).
*   **API Standardization**: Integrates **Springdoc OpenAPI / Swagger UI** for automated interactive REST API docs.

---

## ⚡ Current Capabilities

1.  **Event-Driven Mailers (`KafkaConsumer` & `MailService`)**:
    *   Listens to `auth.send.active` -> Sends HTML activation links to newly registered users.
    *   Listens to `auth.send.otp` -> Sends verification codes for password recovery.
2.  **Real-Time & Persistent Group/Direct Chats (`ChatController` & `MessageService`)**:
    *   Clients subscribe to `/topic/conversation/{conversationId}`.
    *   Messages sent to `/app/chat.sendMessage` are persisted to MySQL and broad-casted in real-time.
3.  **Real-Time Push Notifications**:
    *   Listens to Kafka topic `notification.created`, saves the entry, and pushes it over websocket to `/topic/notifications/{userID}`.
4.  **Promotions & Subscription Broadcaster**:
    *   Allows newsletter signups via `SubscriberController`.
    *   Distributes promo codes to all active subscribers via async email threads (`PromotionService`).

---

## 🚀 Proposed High-Impact Features

### 1. Spring AI Powered E-Commerce Support Chatbot
Introduce an automated, intelligent AI customer assistant that acts as a virtual support agent on the website. If a customer opens a chat and no human agent is available, or if the conversation is routed to a special `staffId = 0` (Chatbot), the system uses Spring AI to answer the customer instantly.

*   **AI Integration**: Add a dependency such as `spring-ai-openai-spring-boot-starter` or `spring-ai-ollama-spring-boot-starter` inside `pom.xml`.
*   **RAG System (Retrieval-Augmented Generation)**: Ingest Furniro's furniture store inventory, FAQs, return policies, and shipping options into a vector database (e.g. PGVector, Redis Vector Search).
*   **Chat Routing**: Create an `AiChatService`. When `ChatController` receives a message where `receiverId` is the Chatbot:
    *   Query the Vector Database for relevant product/policy context.
    *   Feed the prompt + context into Spring AI's `ChatClient`.
    *   Send the generated response back to `/topic/conversation/{conversationId}`.
*   **Human Handover**: If the user asks for a human ("Connect me with a real person"), the AI service changes the `staffId` in the `Conversation` table to a real human staff ID and notifies human agents via a WebSocket alert.

---

### 2. Multi-Channel Resilient Notification Pipeline (with WebSocket Fallback)
Currently, when a notification is created, it is published *exclusively* over WebSockets. If a user is offline, the notification will be stored in the database but they will not see it immediately. We can build a resilient, multi-channel notification engine.

*   **Connection Tracker**: Utilize Redis to maintain a live registry of active WebSocket sessions. When a client connects/disconnects, update the status in Redis.
*   **Fallback Evaluation**: When `KafkaConsumer` receives `notification.created`:
    *   Check Redis if `userID` is online.
    *   If **Online**: Send the payload via `messagingTemplate.convertAndSend()`.
    *   If **Offline**: Route the event to the user's backup channels depending on preference (e.g. immediate fallback email via `MailService` or an SMS via Twilio).

---

### 3. Ephemeral Real-Time Chat Features (Typing Indicators & Read Receipts)
Standard chat applications look static without micro-interactions. Adding real-time typing indicators and immediate push-based read receipts makes the chat feel premium and responsive.

*   **Typing Indicators**:
    *   Add a destination mapping `/chat.typing` in `ChatController`.
    *   When the user types, the client sends a lightweight STOMP header: `{ "senderId": 1, "receiverId": 2, "conversationId": 5, "typing": true }`.
    *   Do **NOT** persist this to MySQL (it is temporary). Broadcast it directly to the receiver's queue.
*   **WebSocket-Based Read Receipts**:
    *   Extend `ChatController` with a `/chat.markRead` mapping.
    *   When a user opens a conversation, send a STOMP message.
    *   The service updates MySQL in the background, and pushes the read receipt `{ "messageId": 45, "readAt": "2026-06-02T..." }` to the sender so their double-checkmarks turn blue instantly.

---

### 4. Segmented & Targeted Promo Marketing (Spring Mail & Redis)
Currently, when a promotion is created, `PromotionService` retrieves all subscribers and sends them emails. E-commerce sites targeting thousands of subscribers will experience heavy performance bottlenecking and higher bounce rates. We should transition to a segmented, queued approach.

*   **Segmentation Engine**: Allow grouping of subscribers (e.g., `VIP`, `New Subscribers`, `Kitchen Lovers`, `Living Room Searchers`).
*   **Rate-Limiting & Task Queuing**: 
    *   Rather than looping through thousands of subscribers inline, push the promotional campaign into a dedicated Kafka topic: `campaign.distribute`.
    *   The `MessageService` consumes this topic in batches (e.g., sending 50 emails every minute using Spring Task Scheduler or Quartz) to avoid triggering SMTP rate limiters.
*   **Dynamic Coupon Generation**: Connect the service with a Coupon module to generate unique individual tracking codes (e.g., `WELCOME-ANH-5432`) rather than a single static code (`WELCOME10`), optimizing marketing conversion tracking.

---

### 5. Distributed Chat History Caching with Redis
To keep WebSocket communication extremely quick, we should avoid querying the MySQL database (`MessageRepository.findAllByConversation`) every single time a chat tab is selected. 

*   **Write-Behind / Cache-Aside Pattern**:
    *   When `createMessage()` is executed, save it to the database, but also append the message to a Redis List (`chat:history:conversationId`).
    *   Keep the size of the Redis List capped at 50 messages to save memory.
*   **Cache Retrieval**:
    *   When a user requests the last 50 messages of a conversation, retrieve it from the Redis List in O(1) time.
    *   If the user scrolls further up to load older historical messages (page > 0), fall back to query the MySQL database.

---

## 🛠️ Priority & Roadmap

1.  **Phase 1 (Interactive UX)**: Ephemeral features (Typing indicators & real-time Read Receipts). Fast to build and creates immediate visual impacts on the frontend.
2.  **Phase 2 (Scalability & Resiliency)**: Redis Chat Caching and Multi-Channel fallback notifications.
3.  **Phase 3 (AI integration)**: Spring AI powered virtual customer assistant.

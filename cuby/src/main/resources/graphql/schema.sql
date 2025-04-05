# src/main/resources/graphql/schema.graphqls

"""
A chat session between a customer and the chatbot
"""
type ChatSession {
    # Unique identifier for the session
    id: ID!

    # The Shopify store ID
    storeId: String!

    # When the session was created
    createdAt: String!

    # When the session was last active
    lastActivity: String!

    # User information if available
    userData: UserData

    # Current status of the session
    status: SessionStatus!

    # Messages in this session
    messages: [ChatMessage!]!

    # Total number of messages in the session
    messageCount: Int!
}

"""
Information about the user/customer
"""
type UserData {
    userId: String
    email: String
    name: String
}

"""
Current status of a chat session
"""
enum SessionStatus {
    ACTIVE
    CLOSED
    TRANSFERRED
}

"""
A message within a chat session
"""
type ChatMessage {
    # Unique identifier for the message
    id: ID!

    # The session this message belongs to
    sessionId: ID!

    # The message content
    content: String!

    # Who sent the message (user or bot)
    sender: MessageSender!

    # When the message was sent
    timestamp: String!

    # Additional metadata for the message (JSON)
    metadata: String
}

"""
Who sent a message
"""
enum MessageSender {
    user
    bot
}

"""
Configuration for a store's chatbot
"""
type ChatConfig {
    # The Shopify store ID
    storeId: String!

    # Initial message shown to users
    welcomeMessage: String

    # Theme (light/dark)
    theme: String

    # Widget position (left/right)
    position: String

    # Whether file attachments are allowed
    allowAttachments: Boolean!

    # Whether product recommendations are enabled
    productRecommendations: Boolean!

    # Whether order lookup is enabled
    orderLookup: Boolean!

    # AI configuration
    aiAssistance: AIConfig!

    # When the config was created
    createdAt: String!

    # When the config was last updated
    updatedAt: String!
}

"""
AI configuration for the chatbot
"""
type AIConfig {
    # Whether AI is enabled
    enabled: Boolean!

    # Which model to use
    model: String

    # Temperature setting (0.0 to 1.0)
    temperature: Float!

    # Maximum tokens per response
    maxTokens: Int!

    # Custom system prompt
    systemPrompt: String
}

"""
Information about a Shopify store
"""
type StoreInfo {
    # The Shopify store ID
    id: String!

    # Store name
    name: String!

    # Store domain
    domain: String!

    # Store currency
    currency: String

    # Contact email
    contactEmail: String

    # Support phone number
    supportPhone: String

    # Store policies
    policies: StorePolicies
}

"""
Store policies
"""
type StorePolicies {
    refundPolicy: String
    privacyPolicy: String
    shippingPolicy: String
    termsOfService: String
}

"""
Product information
"""
type Product {
    # Product ID
    id: String!

    # Product title
    title: String!

    # Product description
    description: String

    # Current price
    price: String!

    # Original price if on sale
    compareAtPrice: String

    # Product image URL
    imageUrl: String

    # Product handle (URL slug)
    handle: String!

    # Product variants
    variants: [ProductVariant!]!
}

"""
Product variant
"""
type ProductVariant {
    # Variant ID
    id: String!

    # Variant title
    title: String!

    # Variant price
    price: String!

    # Whether the variant is in stock
    available: Boolean!
}

"""
Order information
"""
type Order {
    # Order ID
    id: String!

    # Order number (displayed to customers)
    orderNumber: String!

    # Order status
    status: OrderStatus!

    # When the order was created
    createdAt: String!

    # Total price of the order
    totalPrice: String!

    # Tracking information if available
    trackingInfo: TrackingInfo

    # Items in the order
    lineItems: [LineItem!]!
}

"""
Order status
"""
enum OrderStatus {
    unfulfilled
    partial
    fulfilled
    delivered
    cancelled
}

"""
Shipping tracking information
"""
type TrackingInfo {
    # Shipping company
    company: String!

    # Tracking number
    number: String!

    # Tracking URL
    url: String
}

"""
Order line item
"""
type LineItem {
    # Product title
    title: String!

    # Quantity ordered
    quantity: Int!

    # Item price
    price: String!

    # Item image
    imageUrl: String
}

# Queries
type Query {
    # Get a specific chat session by ID
    chatSession(id: ID!): ChatSession

    # Get all chat sessions for a store
    chatSessionsByStore(storeId: String!, limit: Int, offset: Int): [ChatSession!]!

    # Get chat configuration for a store
    chatConfig(storeId: String!): ChatConfig

    # Get store information
    storeInfo(storeId: String!): StoreInfo

    # Search for products
    searchProducts(storeId: String!, query: String!, limit: Int): [Product!]!

    # Get product details
    product(storeId: String!, productId: String!): Product

    # Get order details
    order(storeId: String!, orderNumber: String!, customerEmail: String!): Order
}

# Input for creating a new chat session
input CreateSessionInput {
    storeId: String!
    userData: UserDataInput
}

# Input for user data
input UserDataInput {
    userId: String
    email: String
    name: String
}

# Input for sending a message
input SendMessageInput {
    sessionId: ID!
    content: String!
    storeId: String!
}

# Input for updating chat configuration
input UpdateConfigInput {
    storeId: String!
    welcomeMessage: String
    theme: String
    position: String
    allowAttachments: Boolean
    productRecommendations: Boolean
    orderLookup: Boolean
    aiAssistance: AIConfigInput
}

# Input for AI configuration
input AIConfigInput {
    enabled: Boolean!
    model: String
    temperature: Float
    maxTokens: Int
    systemPrompt: String
}

# Mutations
type Mutation {
    # Create a new chat session
    createChatSession(input: CreateSessionInput!): ChatSession!

    # Send a message in a chat session
    sendMessage(input: SendMessageInput!): ChatMessage!

    # Update chat configuration
    updateChatConfig(input: UpdateConfigInput!): ChatConfig!

    # Close a chat session
    closeChatSession(id: ID!): Boolean!
}

# Subscriptions
type Subscription {
    # Subscribe to new messages in a session
    messageAdded(sessionId: ID!): ChatMessage!

    # Subscribe to session status changes
    sessionStatusChanged(storeId: String!): ChatSession!
}
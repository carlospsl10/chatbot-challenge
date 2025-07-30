# Order Status Chatbot - Frontend

A React-based web application for the AI-powered Order Status Chatbot. This frontend provides a modern, responsive interface for customers to interact with the chatbot and check their order status.

## Features

- 🔐 **User Authentication** - Secure login with JWT tokens
- 💬 **Real-time Chat** - WebSocket-based chat interface
- 🎨 **Modern UI** - Material-UI components for beautiful design
- 📱 **Responsive Design** - Works on desktop and mobile devices
- 🔄 **TypeScript** - Type-safe development
- ⚡ **Fast Performance** - Optimized React components

## Technology Stack

- **React 18** - UI framework
- **TypeScript** - Type safety
- **Material-UI** - Component library
- **React Router** - Client-side routing
- **Socket.io-client** - Real-time communication
- **Axios** - HTTP client for API calls

## Project Structure

```
frontend/
├── public/
│   ├── index.html
│   └── manifest.json
├── src/
│   ├── components/
│   │   ├── auth/
│   │   │   └── Login.tsx
│   │   ├── chat/
│   │   │   └── Chat.tsx
│   │   └── layout/
│   │       └── Layout.tsx
│   ├── App.tsx
│   ├── App.css
│   ├── index.tsx
│   ├── index.css
│   └── reportWebVitals.ts
├── package.json
└── README.md
```

## Getting Started

### Prerequisites

- Node.js (v16 or higher)
- npm or yarn

### Installation

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm start
   ```

4. Open [http://localhost:3000](http://localhost:3000) in your browser.

### Available Scripts

- `npm start` - Start development server
- `npm build` - Build for production
- `npm test` - Run tests
- `npm eject` - Eject from Create React App

## Development

### Component Structure

- **Login** - Authentication form with email/password
- **Chat** - Main chat interface with message history
- **Layout** - App layout with header and navigation

### API Integration

The frontend communicates with the Spring Boot backend through:

- **REST APIs** - For authentication and data retrieval
- **WebSocket** - For real-time chat communication

### Environment Variables

Create a `.env` file in the frontend directory:

```env
REACT_APP_API_URL=http://localhost:8080
REACT_APP_WS_URL=ws://localhost:8080/ws/chat
```

## Deployment

### Production Build

1. Build the application:
   ```bash
   npm run build
   ```

2. The build artifacts will be in the `build/` directory.

### Docker Deployment

The frontend can be deployed using Docker:

```dockerfile
FROM node:16-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build
FROM nginx:alpine
COPY --from=0 /app/build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

## Contributing

1. Follow the existing code style
2. Add TypeScript types for all new components
3. Test your changes before submitting
4. Update documentation as needed

## Troubleshooting

### Common Issues

1. **Port 3000 already in use**
   - Kill the process using the port or change the port in package.json

2. **Module not found errors**
   - Run `npm install` to ensure all dependencies are installed

3. **TypeScript errors**
   - Check that all imports have proper type definitions

## License

This project is part of the Order Status Chatbot MVP. 
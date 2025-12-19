# AI Flashcards Frontend

Frontend application for the AI Flashcards app, built with Next.js and NextAuth.

## Environment Variables

The following environment variables are required:

### Authentication (Keycloak)

- `KEYCLOAK_CLIENT_ID` - Keycloak client ID
- `KEYCLOAK_CLIENT_SECRET` - Keycloak client secret
- `KEYCLOAK_ISSUER` - Keycloak issuer URL (e.g., `https://keycloak.example.com/realms/your-realm`)
- `NEXTAUTH_SECRET` - Secret for NextAuth session encryption

### Backend API

- `NEXT_PUBLIC_BACKEND_URL` - Backend API base URL (defaults to `http://localhost:8080` if not set)

### AI Generation (OpenRouter)

- `OPENROUTER_API_KEY` - OpenRouter API key for AI flashcard generation (get from https://openrouter.ai/keys)

### Optional

- `DEBUG_AUTH` - Set to `"true"` to enable authentication debug logging

## Features

- **Automatic User Sync**: When a user signs in or signs up, the frontend automatically syncs the user to the backend by calling the `/api/users` endpoint with the access token. This ensures the user exists in the database.

## Development

```bash
npm install
npm run dev
```

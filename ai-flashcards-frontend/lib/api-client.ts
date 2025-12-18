import { UserDto } from "@/types/user";
import { FolderDto, PageResponse } from "@/types/folder";
import { CardDto } from "@/types/card";

const BACKEND_URL =
  process.env.NEXT_PUBLIC_BACKEND_URL || "http://localhost:8080";

export interface ApiError {
  message: string;
  status?: number;
}

/**
 * Syncs the user to the backend by creating or finding the user in the database.
 * @param accessToken - The JWT access token from Keycloak
 * @returns The user data from the backend, or null if the request failed
 * @throws {ApiError} If the request fails with a non-2xx status
 */
export async function syncUserToBackend(
  accessToken: string
): Promise<UserDto | null> {
  try {
    const response = await fetch(`${BACKEND_URL}/api/users`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${accessToken}`,
      },
    });

    if (!response.ok) {
      const error: ApiError = {
        message: `Failed to sync user: ${response.statusText}`,
        status: response.status,
      };

      // Handle specific error cases
      if (response.status === 401) {
        error.message = "Authentication failed. Please sign in again.";
      } else if (response.status >= 500) {
        error.message = "Server error. Please try again later.";
      }

      throw error;
    }

    const userData: UserDto = await response.json();
    return userData;
  } catch (error) {
    // Re-throw ApiError as-is
    if (error && typeof error === "object" && "message" in error) {
      throw error;
    }

    // Handle network errors
    const networkError: ApiError = {
      message:
        error instanceof Error
          ? `Network error: ${error.message}`
          : "Network error: Failed to connect to backend",
    };
    throw networkError;
  }
}

/**
 * Fetches all folders for the authenticated user with pagination.
 * @param accessToken - The JWT access token from Keycloak
 * @param page - The page number (0-indexed)
 * @param size - The number of items per page
 * @returns The paginated folder data from the backend
 * @throws {ApiError} If the request fails with a non-2xx status
 */
export async function getAllFolders(
  accessToken: string,
  page: number,
  size: number
): Promise<PageResponse<FolderDto>> {
  try {
    const response = await fetch(
      `${BACKEND_URL}/api/folders?page=${page}&size=${size}`,
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${accessToken}`,
        },
      }
    );

    if (!response.ok) {
      const error: ApiError = {
        message: `Failed to fetch folders: ${response.statusText}`,
        status: response.status,
      };

      // Handle specific error cases
      if (response.status === 401) {
        error.message = "Authentication failed. Please sign in again.";
      } else if (response.status >= 500) {
        error.message = "Server error. Please try again later.";
      }

      throw error;
    }

    const folderData: PageResponse<FolderDto> = await response.json();
    return folderData;
  } catch (error) {
    // Re-throw ApiError as-is
    if (error && typeof error === "object" && "message" in error) {
      throw error;
    }

    // Handle network errors
    const networkError: ApiError = {
      message:
        error instanceof Error
          ? `Network error: ${error.message}`
          : "Network error: Failed to connect to backend",
    };
    throw networkError;
  }
}

/**
 * Creates a new folder for the authenticated user.
 * @param accessToken - The JWT access token from Keycloak
 * @param name - The name of the folder to create
 * @returns The created folder data from the backend
 * @throws {ApiError} If the request fails with a non-2xx status
 */
export async function createFolder(
  accessToken: string,
  name: string
): Promise<FolderDto> {
  try {
    const response = await fetch(`${BACKEND_URL}/api/folders`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${accessToken}`,
      },
      body: JSON.stringify({ name }),
    });

    if (!response.ok) {
      const error: ApiError = {
        message: `Failed to create folder: ${response.statusText}`,
        status: response.status,
      };

      // Handle specific error cases
      if (response.status === 401) {
        error.message = "Authentication failed. Please sign in again.";
      } else if (response.status >= 500) {
        error.message = "Server error. Please try again later.";
      }

      throw error;
    }

    const folderData: FolderDto = await response.json();
    return folderData;
  } catch (error) {
    // Re-throw ApiError as-is
    if (error && typeof error === "object" && "message" in error) {
      throw error;
    }

    // Handle network errors
    const networkError: ApiError = {
      message:
        error instanceof Error
          ? `Network error: ${error.message}`
          : "Network error: Failed to connect to backend",
    };
    throw networkError;
  }
}

/**
 * Fetches all cards for a specific folder with pagination.
 * @param accessToken - The JWT access token from Keycloak
 * @param folderId - The ID of the folder
 * @param page - The page number (0-indexed), defaults to 0
 * @param size - The number of items per page, defaults to 10
 * @returns The paginated card data from the backend
 * @throws {ApiError} If the request fails with a non-2xx status
 */
export async function getAllCardsOfFolder(
  accessToken: string,
  folderId: number,
  page: number = 0,
  size: number = 10
): Promise<PageResponse<CardDto>> {
  try {
    const response = await fetch(
      `${BACKEND_URL}/api/folders/${folderId}?page=${page}&size=${size}`,
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${accessToken}`,
        },
      }
    );

    if (!response.ok) {
      const error: ApiError = {
        message: `Failed to fetch cards: ${response.statusText}`,
        status: response.status,
      };

      // Handle specific error cases
      if (response.status === 401) {
        error.message = "Authentication failed. Please sign in again.";
      } else if (response.status === 404) {
        error.message = "Folder not found.";
      } else if (response.status >= 500) {
        error.message = "Server error. Please try again later.";
      }

      throw error;
    }

    const cardData: PageResponse<CardDto> = await response.json();
    return cardData;
  } catch (error) {
    // Re-throw ApiError as-is
    if (error && typeof error === "object" && "message" in error) {
      throw error;
    }

    // Handle network errors
    const networkError: ApiError = {
      message:
        error instanceof Error
          ? `Network error: ${error.message}`
          : "Network error: Failed to connect to backend",
    };
    throw networkError;
  }
}

/**
 * Creates a new card in a specific folder.
 * @param accessToken - The JWT access token from Keycloak
 * @param folderId - The ID of the folder
 * @param question - The question text for the card
 * @param answer - The answer text for the card
 * @returns The created card data from the backend
 * @throws {ApiError} If the request fails with a non-2xx status
 */
export async function createCard(
  accessToken: string,
  folderId: number,
  question: string,
  answer: string
): Promise<CardDto> {
  try {
    const response = await fetch(
      `${BACKEND_URL}/api/folders/${folderId}/cards`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${accessToken}`,
        },
        body: JSON.stringify({ question, answer }),
      }
    );

    if (!response.ok) {
      const error: ApiError = {
        message: `Failed to create card: ${response.statusText}`,
        status: response.status,
      };

      // Handle specific error cases
      if (response.status === 401) {
        error.message = "Authentication failed. Please sign in again.";
      } else if (response.status === 404) {
        error.message = "Folder not found.";
      } else if (response.status >= 500) {
        error.message = "Server error. Please try again later.";
      }

      throw error;
    }

    const cardData: CardDto = await response.json();
    return cardData;
  } catch (error) {
    // Re-throw ApiError as-is
    if (error && typeof error === "object" && "message" in error) {
      throw error;
    }

    // Handle network errors
    const networkError: ApiError = {
      message:
        error instanceof Error
          ? `Network error: ${error.message}`
          : "Network error: Failed to connect to backend",
    };
    throw networkError;
  }
}

/**
 * Updates an existing card in a specific folder.
 * @param accessToken - The JWT access token from Keycloak
 * @param folderId - The ID of the folder
 * @param cardId - The ID of the card to update
 * @param question - The updated question text for the card
 * @param answer - The updated answer text for the card
 * @throws {ApiError} If the request fails with a non-2xx status
 */
export async function updateCard(
  accessToken: string,
  folderId: number,
  cardId: number,
  question: string,
  answer: string
): Promise<void> {
  try {
    const response = await fetch(
      `${BACKEND_URL}/api/folders/${folderId}/cards/${cardId}`,
      {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${accessToken}`,
        },
        body: JSON.stringify({ question, answer }),
      }
    );

    if (!response.ok) {
      const error: ApiError = {
        message: `Failed to update card: ${response.statusText}`,
        status: response.status,
      };

      // Handle specific error cases
      if (response.status === 401) {
        error.message = "Authentication failed. Please sign in again.";
      } else if (response.status === 404) {
        error.message = "Card or folder not found.";
      } else if (response.status >= 500) {
        error.message = "Server error. Please try again later.";
      }

      throw error;
    }

    // Response is 200 OK with no body (void)
  } catch (error) {
    // Re-throw ApiError as-is
    if (error && typeof error === "object" && "message" in error) {
      throw error;
    }

    // Handle network errors
    const networkError: ApiError = {
      message:
        error instanceof Error
          ? `Network error: ${error.message}`
          : "Network error: Failed to connect to backend",
    };
    throw networkError;
  }
}

/**
 * Deletes a card from a specific folder.
 * @param accessToken - The JWT access token from Keycloak
 * @param folderId - The ID of the folder
 * @param cardId - The ID of the card to delete
 * @throws {ApiError} If the request fails with a non-2xx status
 */
export async function deleteCard(
  accessToken: string,
  folderId: number,
  cardId: number
): Promise<void> {
  try {
    const response = await fetch(
      `${BACKEND_URL}/api/folders/${folderId}/cards/${cardId}`,
      {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${accessToken}`,
        },
      }
    );

    if (!response.ok) {
      const error: ApiError = {
        message: `Failed to delete card: ${response.statusText}`,
        status: response.status,
      };

      // Handle specific error cases
      if (response.status === 401) {
        error.message = "Authentication failed. Please sign in again.";
      } else if (response.status === 404) {
        error.message = "Card or folder not found.";
      } else if (response.status >= 500) {
        error.message = "Server error. Please try again later.";
      }

      throw error;
    }

    // Response is 200 OK with no body (void)
  } catch (error) {
    // Re-throw ApiError as-is
    if (error && typeof error === "object" && "message" in error) {
      throw error;
    }

    // Handle network errors
    const networkError: ApiError = {
      message:
        error instanceof Error
          ? `Network error: ${error.message}`
          : "Network error: Failed to connect to backend",
    };
    throw networkError;
  }
}

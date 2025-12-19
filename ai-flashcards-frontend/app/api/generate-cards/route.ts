import { NextRequest, NextResponse } from "next/server";
import { auth } from "@/lib/auth";
import { createBulkCards } from "@/lib/api-client";

interface FlashcardResponse {
  flashcards: Array<{
    question: string;
    answer: string;
  }>;
}

export async function POST(request: NextRequest) {
  try {
    // 1. Authenticate user
    const session = await auth();
    if (!session?.accessToken) {
      return NextResponse.json(
        { error: "Unauthorized" },
        { status: 401 }
      );
    }

    // 2. Get request body
    const body = await request.json();
    const { prompt, folderId } = body;

    if (!prompt || !folderId) {
      return NextResponse.json(
        { error: "Missing prompt or folderId" },
        { status: 400 }
      );
    }

    const folderIdNum = parseInt(folderId, 10);
    if (isNaN(folderIdNum)) {
      return NextResponse.json(
        { error: "Invalid folderId" },
        { status: 400 }
      );
    }

    // 3. Validate API key
    const apiKey = process.env.OPENROUTER_API_KEY;
    if (!apiKey) {
      console.error("OPENROUTER_API_KEY is not set");
      return NextResponse.json(
        { error: "AI service is not configured" },
        { status: 500 }
      );
    }

    // 4. Prepare OpenRouter request
    const openRouterRequest = {
      model: "openai/gpt-5-nano",
      messages: [
        {
          role: "system",
          content:
            "You are a flashcard generator. Create educational flashcards with clear questions and comprehensive answers.\n\nIMPORTANT RULES:\n- You can create a MAXIMUM of 10 flashcards\n- Each flashcard must have a clear, specific question\n- Each answer must be comprehensive and educational\n- Questions should test understanding, not just recall\n- Return ONLY valid JSON in this exact format: { \"flashcards\": [{\"question\": \"...\", \"answer\": \"...\"}] }\n- Do not include any explanatory text outside the JSON\n- Ensure the JSON is valid and parseable",
        },
        {
          role: "user",
          content: `Create flashcards about: ${prompt}. Return only valid JSON.`,
        },
      ],
      response_format: {
        type: "json_schema",
        json_schema: {
          name: "flashcard_generator",
          strict: true,
          schema: {
            type: "object",
            properties: {
              flashcards: {
                type: "array",
                items: {
                  type: "object",
                  properties: {
                    question: { type: "string" },
                    answer: { type: "string" },
                  },
                  required: ["question", "answer"],
                  additionalProperties: false,
                },
              },
            },
            required: ["flashcards"],
            additionalProperties: false,
          },
        },
      },
    };

    // Call OpenRouter API
    const openRouterResponse = await fetch(
      "https://openrouter.ai/api/v1/chat/completions",
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${apiKey}`,
          "HTTP-Referer": process.env.NEXT_PUBLIC_SITE_URL || "",
          "X-Title": "AI Flashcards App",
        },
        body: JSON.stringify(openRouterRequest),
      }
    );

    if (!openRouterResponse.ok) {
      const errorText = await openRouterResponse.text();
      console.error("OpenRouter API error:", errorText);
      return NextResponse.json(
        {
          error: "Failed to generate flashcards. Please try again.",
          debug: {
            requestToOpenRouter: {
              prompt,
              model: openRouterRequest.model,
              systemMessage: openRouterRequest.messages[0].content,
              userMessage: openRouterRequest.messages[1].content,
            },
            openRouterError: errorText,
          },
        },
        { status: 500 }
      );
    }

    const openRouterData = await openRouterResponse.json();
    const content = openRouterData.choices?.[0]?.message?.content;

    if (!content) {
      console.error("No content in OpenRouter response:", openRouterData);
      return NextResponse.json(
        {
          error: "No content from AI. Please try again.",
          debug: {
            requestToOpenRouter: {
              prompt,
              model: openRouterRequest.model,
              systemMessage: openRouterRequest.messages[0].content,
              userMessage: openRouterRequest.messages[1].content,
            },
            openRouterResponse: {
              raw: openRouterData,
            },
          },
        },
        { status: 500 }
      );
    }

    // 5. Parse JSON response
    let parsed: FlashcardResponse;
    try {
      parsed = JSON.parse(content);
    } catch (parseError) {
      console.error("Failed to parse AI response:", content);
      return NextResponse.json(
        {
          error: "Invalid response format. Please try again.",
          debug: {
            requestToOpenRouter: {
              prompt,
              model: openRouterRequest.model,
              systemMessage: openRouterRequest.messages[0].content,
              userMessage: openRouterRequest.messages[1].content,
            },
            openRouterResponse: {
              raw: openRouterData,
              content: content,
            },
            parseError: parseError instanceof Error ? parseError.message : String(parseError),
          },
        },
        { status: 500 }
      );
    }

    // 6. Validate parsed data
    if (
      !parsed.flashcards ||
      !Array.isArray(parsed.flashcards) ||
      parsed.flashcards.length === 0
    ) {
      return NextResponse.json(
        {
          error: "No flashcards generated. Please try again.",
          debug: {
            requestToOpenRouter: {
              prompt,
              model: openRouterRequest.model,
              systemMessage: openRouterRequest.messages[0].content,
              userMessage: openRouterRequest.messages[1].content,
            },
            openRouterResponse: {
              raw: openRouterData,
              content: content,
            },
            parsedFlashcards: parsed,
          },
        },
        { status: 500 }
      );
    }

    // Limit to 10 cards as per backend validation
    const cardsToCreate = parsed.flashcards.slice(0, 10).map((card) => ({
      question: card.question.trim(),
      answer: card.answer.trim(),
    }));

    // 7. Prepare backend request
    const backendRequest = { cards: cardsToCreate };

    // Create cards in backend
    try {
      const createdCards = await createBulkCards(
        session.accessToken as string,
        folderIdNum,
        cardsToCreate
      );

      return NextResponse.json({
        success: true,
        created: createdCards.length,
        message: `Successfully created and stored ${createdCards.length} flashcards!`,
        debug: {
          requestToOpenRouter: {
            prompt,
            model: openRouterRequest.model,
            systemMessage: openRouterRequest.messages[0].content,
            userMessage: openRouterRequest.messages[1].content,
          },
          openRouterResponse: {
            raw: openRouterData,
            content: content,
          },
          parsedFlashcards: parsed.flashcards,
          cardsToCreate: cardsToCreate,
          requestToBackend: {
            folderId: folderIdNum,
            cards: backendRequest,
          },
          backendResponse: {
            created: createdCards.length,
            cards: createdCards,
          },
        },
      });
    } catch (backendError) {
      console.error("Backend error:", backendError);
      const errorMessage =
        backendError && typeof backendError === "object" && "message" in backendError
          ? (backendError.message as string)
          : "Failed to save flashcards to backend.";

      return NextResponse.json(
        {
          success: false,
          created: 0,
          error: errorMessage,
          debug: {
            requestToOpenRouter: {
              prompt,
              model: openRouterRequest.model,
              systemMessage: openRouterRequest.messages[0].content,
              userMessage: openRouterRequest.messages[1].content,
            },
            openRouterResponse: {
              raw: openRouterData,
              content: content,
            },
            parsedFlashcards: parsed.flashcards,
            cardsToCreate: cardsToCreate,
            requestToBackend: {
              folderId: folderIdNum,
              cards: backendRequest,
            },
            backendError: errorMessage,
          },
        },
        { status: 500 }
      );
    }
  } catch (error) {
    console.error("Error generating cards:", error);
    return NextResponse.json(
      {
        success: false,
        created: 0,
        error:
          error instanceof Error ? error.message : "Internal server error",
      },
      { status: 500 }
    );
  }
}


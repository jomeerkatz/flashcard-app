"use client";

import { useParams, useRouter } from "next/navigation";
import { useSession } from "next-auth/react";
import { useState } from "react";
import Header from "@/components/Header";
import Footer from "@/components/Footer";

type StatusType = "idle" | "generating" | "success" | "error";

export default function GenerateCardsPage() {
  const params = useParams();
  const router = useRouter();
  const { data: session, status: sessionStatus } = useSession();
  const folderId = params?.folderId as string;
  const [prompt, setPrompt] = useState("");
  const [status, setStatus] = useState<StatusType>("idle");
  const [statusMessage, setStatusMessage] = useState("");
  const [createdCount, setCreatedCount] = useState(0);
  const [debugInfo, setDebugInfo] = useState<any>(null);
  const [showDebug, setShowDebug] = useState(false);

  const handleGenerate = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!prompt.trim()) {
      setStatus("error");
      setStatusMessage("Please enter a prompt");
      return;
    }

    if (!session?.accessToken || !folderId) {
      setStatus("error");
      setStatusMessage("Authentication required");
      return;
    }

    try {
      setStatus("generating");
      setStatusMessage("Generating flashcards...");

      const response = await fetch("/api/generate-cards", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          prompt: prompt.trim(),
          folderId: parseInt(folderId, 10),
        }),
      });

      const data = await response.json();

      // Store debug info if available
      if (data.debug) {
        setDebugInfo(data.debug);
        setShowDebug(true);
      }

      if (!response.ok || !data.success) {
        setStatus("error");
        setStatusMessage(
          data.error || "Failed to generate flashcards. Please try again."
        );
        return;
      }

      // The API route already generates and saves to backend
      setStatus("success");
      setCreatedCount(data.created || 0);
      setStatusMessage(
        data.message || `Successfully created and stored ${data.created} flashcards!`
      );
    } catch (error) {
      console.error("Error generating cards:", error);
      setStatus("error");
      setStatusMessage(
        error instanceof Error
          ? error.message
          : "An error occurred. Please try again."
      );
    }
  };

  if (sessionStatus === "loading") {
    return (
      <div className="flex flex-col min-h-screen">
        <Header />
        <div className="flex-1 flex items-center justify-center py-20 px-4">
          <div className="text-center">
            <div className="w-16 h-16 border-4 border-cyan-500 border-t-transparent animate-spin mx-auto mb-4" />
            <p className="text-slate-400">Loading...</p>
          </div>
        </div>
        <Footer />
      </div>
    );
  }

  if (sessionStatus === "unauthenticated") {
    return (
      <div className="flex flex-col min-h-screen">
        <Header />
        <div className="flex-1 flex items-center justify-center py-20 px-4">
          <div className="text-center">
            <p className="text-slate-400">Please sign in to generate cards.</p>
          </div>
        </div>
        <Footer />
      </div>
    );
  }

  return (
    <div className="flex flex-col min-h-screen">
      <Header />
      <div className="flex-1 py-12 px-4">
        <div className="container mx-auto max-w-4xl">
          {/* Back Button */}
          <button
            onClick={() => router.push(`/folders/${folderId}`)}
            className="mb-8 flex items-center gap-2 text-cyan-400 hover:text-cyan-300 transition-colors focus:outline-none focus:ring-2 focus:ring-cyan-500 focus:ring-offset-2 focus:ring-offset-black"
            aria-label="Back to folder"
          >
            <svg
              className="w-5 h-5"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M10 19l-7-7m0 0l7-7m-7 7h18"
              />
            </svg>
            <span className="font-semibold uppercase tracking-wide text-sm">
              Back to Folder
            </span>
          </button>

          {/* Main Content - Centered */}
          <div className="flex flex-col items-center justify-center min-h-[60vh]">
            {/* Header Section */}
            <div className="text-center mb-12 space-y-4">
              <h1 className="text-4xl sm:text-5xl font-bold text-white mb-4 tracking-tight">
                AI Flashcard Generator
              </h1>
              <div className="w-24 h-1 bg-gradient-to-r from-transparent via-cyan-500 to-transparent mx-auto mb-6" />
              <p className="text-slate-400 text-lg max-w-2xl mx-auto leading-relaxed">
                Enter a topic or subject below, and our AI will create up to 10
                educational flashcards for you. The flashcards will be
                automatically saved to your folder.
              </p>
            </div>

            {/* Input Form */}
            <form
              onSubmit={handleGenerate}
              className="w-full max-w-2xl space-y-8"
            >
              <div className="relative">
                <textarea
                  value={prompt}
                  onChange={(e) => {
                    setPrompt(e.target.value);
                    if (status === "error") {
                      setStatus("idle");
                      setStatusMessage("");
                    }
                  }}
                  placeholder="e.g., Create 10 flashcards about fundamentals of Spring Boot and Java"
                  rows={4}
                  disabled={status === "generating"}
                  className="w-full px-6 py-4 bg-slate-900/50 border-2 border-slate-700 text-white placeholder-slate-500 focus:outline-none focus:border-cyan-500 focus:ring-2 focus:ring-cyan-500 focus:ring-offset-2 focus:ring-offset-black transition-all duration-200 resize-none backdrop-blur-sm disabled:opacity-50 disabled:cursor-not-allowed"
                  autoFocus
                />
                {/* Angular corner accent */}
                <div className="absolute top-0 left-0 w-4 h-4 border-t-2 border-l-2 border-cyan-500 opacity-50" />
                <div className="absolute top-0 right-0 w-4 h-4 border-t-2 border-r-2 border-cyan-500 opacity-50" />
                <div className="absolute bottom-0 left-0 w-4 h-4 border-b-2 border-l-2 border-cyan-500 opacity-50" />
                <div className="absolute bottom-0 right-0 w-4 h-4 border-b-2 border-r-2 border-cyan-500 opacity-50" />
              </div>

              {/* Generate Button */}
              <button
                type="submit"
                  disabled={!prompt.trim() || status === "generating"}
                className="w-full px-8 py-4 bg-transparent border-2 border-cyan-500 text-cyan-500 hover:bg-cyan-500 hover:text-black disabled:border-slate-700 disabled:text-slate-600 disabled:cursor-not-allowed disabled:hover:bg-transparent disabled:hover:text-slate-600 font-semibold transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-cyan-500 focus:ring-offset-2 focus:ring-offset-black uppercase tracking-wide text-sm relative overflow-hidden group"
              >
                <span className="relative z-10 flex items-center justify-center gap-2">
                  {status === "generating" ? (
                    <>
                      <div className="w-5 h-5 border-2 border-current border-t-transparent animate-spin" />
                      Generating...
                    </>
                  ) : (
                    <>
                      <svg
                        className="w-5 h-5"
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth={2}
                          d="M13 10V3L4 14h7v7l9-11h-7z"
                        />
                      </svg>
                      Generate Flashcards
                    </>
                  )}
                </span>
                {/* Futuristic hover effect */}
                <div className="absolute inset-0 bg-cyan-500 transform scale-x-0 group-hover:scale-x-100 transition-transform duration-300 origin-left" />
              </button>
            </form>

            {/* Status Display */}
            {status !== "idle" && (
              <div className="w-full max-w-2xl mt-8">
                {status === "generating" ? (
                  <div className="bg-slate-900/50 border-2 border-cyan-500/50 p-6 backdrop-blur-sm">
                    <div className="flex items-center gap-4">
                      <div className="w-8 h-8 border-2 border-cyan-500 border-t-transparent animate-spin flex-shrink-0" />
                      <p className="text-cyan-400 font-semibold">
                        {statusMessage}
                      </p>
                    </div>
                  </div>
                ) : status === "success" ? (
                  <div className="bg-green-900/30 border-2 border-green-600 p-6 backdrop-blur-sm">
                    <div className="flex items-start gap-4">
                      <svg
                        className="w-6 h-6 text-green-400 flex-shrink-0 mt-0.5"
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth={2}
                          d="M5 13l4 4L19 7"
                        />
                      </svg>
                      <div className="flex-1">
                        <p className="text-green-400 font-semibold mb-1">
                          {statusMessage}
                        </p>
                        <button
                          onClick={() => router.push(`/folders/${folderId}`)}
                          className="mt-4 px-6 py-2 bg-transparent border-2 border-green-500 text-green-500 hover:bg-green-500 hover:text-black font-semibold transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-offset-2 focus:ring-offset-black uppercase tracking-wide text-sm"
                        >
                          View Cards
                        </button>
                        <button
                          onClick={() => {
                            setPrompt("");
                            setStatus("idle");
                            setStatusMessage("");
                            setCreatedCount(0);
                            setDebugInfo(null);
                            setShowDebug(false);
                          }}
                          className="mt-4 ml-4 px-6 py-2 bg-transparent border-2 border-cyan-500 text-cyan-500 hover:bg-cyan-500 hover:text-black font-semibold transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-cyan-500 focus:ring-offset-2 focus:ring-offset-black uppercase tracking-wide text-sm"
                        >
                          Generate More
                        </button>
                      </div>
                    </div>
                  </div>
                ) : (
                  <div className="bg-red-900/30 border-2 border-red-600 p-6 backdrop-blur-sm">
                    <div className="flex items-start gap-4">
                      <svg
                        className="w-6 h-6 text-red-400 flex-shrink-0 mt-0.5"
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth={2}
                          d="M6 18L18 6M6 6l12 12"
                        />
                      </svg>
                      <div className="flex-1">
                        <p className="text-red-400 font-semibold mb-1">
                          {statusMessage}
                        </p>
                        <button
                          onClick={() => {
                            setStatus("idle");
                            setStatusMessage("");
                            setDebugInfo(null);
                            setShowDebug(false);
                          }}
                          className="mt-4 px-6 py-2 bg-transparent border-2 border-red-500 text-red-500 hover:bg-red-500 hover:text-black font-semibold transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2 focus:ring-offset-black uppercase tracking-wide text-sm"
                        >
                          Try Again
                        </button>
                      </div>
                    </div>
                  </div>
                )}
              </div>
            )}

            {/* Debug Panel */}
            {debugInfo && (
              <div className="w-full max-w-2xl mt-8">
                <button
                  onClick={() => setShowDebug(!showDebug)}
                  className="w-full px-6 py-3 bg-slate-900/50 border-2 border-slate-700 text-slate-400 hover:border-cyan-500 hover:text-cyan-400 transition-all duration-200 flex items-center justify-between backdrop-blur-sm"
                >
                  <span className="font-semibold uppercase tracking-wide text-sm flex items-center gap-2">
                    <svg
                      className="w-5 h-5"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
                      />
                    </svg>
                    Debug Information
                  </span>
                  <svg
                    className={`w-5 h-5 transition-transform duration-200 ${
                      showDebug ? "rotate-180" : ""
                    }`}
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M19 9l-7 7-7-7"
                    />
                  </svg>
                </button>

                {showDebug && (
                  <div className="mt-2 bg-slate-900/80 border-2 border-slate-700 p-6 backdrop-blur-sm space-y-6">
                    {/* Request to OpenRouter */}
                    {debugInfo.requestToOpenRouter && (
                      <div>
                        <h3 className="text-cyan-400 font-semibold mb-3 uppercase text-sm tracking-wide flex items-center gap-2">
                          <div className="w-2 h-2 bg-cyan-500" />
                          Request to OpenRouter
                        </h3>
                        <div className="bg-slate-950/50 p-4 border border-slate-800 rounded">
                          <pre className="text-xs text-slate-300 overflow-x-auto">
                            {JSON.stringify(
                              {
                                prompt: debugInfo.requestToOpenRouter.prompt,
                                model: debugInfo.requestToOpenRouter.model,
                                userMessage:
                                  debugInfo.requestToOpenRouter.userMessage,
                              },
                              null,
                              2
                            )}
                          </pre>
                        </div>
                        {debugInfo.requestToOpenRouter.systemMessage && (
                          <details className="mt-2">
                            <summary className="text-slate-400 text-xs cursor-pointer hover:text-cyan-400">
                              View System Message
                            </summary>
                            <div className="mt-2 bg-slate-950/50 p-4 border border-slate-800 rounded">
                              <pre className="text-xs text-slate-300 whitespace-pre-wrap">
                                {debugInfo.requestToOpenRouter.systemMessage}
                              </pre>
                            </div>
                          </details>
                        )}
                      </div>
                    )}

                    {/* OpenRouter Response */}
                    {debugInfo.openRouterResponse && (
                      <div>
                        <h3 className="text-cyan-400 font-semibold mb-3 uppercase text-sm tracking-wide flex items-center gap-2">
                          <div className="w-2 h-2 bg-cyan-500" />
                          OpenRouter Response
                        </h3>
                        <details className="mb-2">
                          <summary className="text-slate-400 text-xs cursor-pointer hover:text-cyan-400 mb-2">
                            View Raw Response
                          </summary>
                          <div className="bg-slate-950/50 p-4 border border-slate-800 rounded">
                            <pre className="text-xs text-slate-300 overflow-x-auto">
                              {JSON.stringify(
                                debugInfo.openRouterResponse.raw,
                                null,
                                2
                              )}
                            </pre>
                          </div>
                        </details>
                        <div className="bg-slate-950/50 p-4 border border-slate-800 rounded">
                          <pre className="text-xs text-slate-300 overflow-x-auto">
                            {JSON.stringify(
                              debugInfo.openRouterResponse.content,
                              null,
                              2
                            )}
                          </pre>
                        </div>
                      </div>
                    )}

                    {/* Parsed Flashcards */}
                    {debugInfo.parsedFlashcards && (
                      <div>
                        <h3 className="text-cyan-400 font-semibold mb-3 uppercase text-sm tracking-wide flex items-center gap-2">
                          <div className="w-2 h-2 bg-cyan-500" />
                          Parsed Flashcards ({debugInfo.parsedFlashcards.length})
                        </h3>
                        <div className="bg-slate-950/50 p-4 border border-slate-800 rounded">
                          <pre className="text-xs text-slate-300 overflow-x-auto">
                            {JSON.stringify(
                              debugInfo.parsedFlashcards,
                              null,
                              2
                            )}
                          </pre>
                        </div>
                      </div>
                    )}

                    {/* Cards to Create */}
                    {debugInfo.cardsToCreate && (
                      <div>
                        <h3 className="text-cyan-400 font-semibold mb-3 uppercase text-sm tracking-wide flex items-center gap-2">
                          <div className="w-2 h-2 bg-cyan-500" />
                          Cards Prepared for Backend (
                          {debugInfo.cardsToCreate.length})
                        </h3>
                        <div className="bg-slate-950/50 p-4 border border-slate-800 rounded">
                          <pre className="text-xs text-slate-300 overflow-x-auto">
                            {JSON.stringify(
                              debugInfo.cardsToCreate,
                              null,
                              2
                            )}
                          </pre>
                        </div>
                      </div>
                    )}

                    {/* Request to Backend */}
                    {debugInfo.requestToBackend && (
                      <div>
                        <h3 className="text-cyan-400 font-semibold mb-3 uppercase text-sm tracking-wide flex items-center gap-2">
                          <div className="w-2 h-2 bg-cyan-500" />
                          Request to Backend
                        </h3>
                        <div className="bg-slate-950/50 p-4 border border-slate-800 rounded">
                          <pre className="text-xs text-slate-300 overflow-x-auto">
                            {JSON.stringify(
                              {
                                endpoint: `POST /api/folders/${debugInfo.requestToBackend.folderId}/cards/bulk`,
                                body: debugInfo.requestToBackend.cards,
                              },
                              null,
                              2
                            )}
                          </pre>
                        </div>
                      </div>
                    )}

                    {/* Backend Response */}
                    {debugInfo.backendResponse && (
                      <div>
                        <h3 className="text-green-400 font-semibold mb-3 uppercase text-sm tracking-wide flex items-center gap-2">
                          <div className="w-2 h-2 bg-green-500" />
                          Backend Response ({debugInfo.backendResponse.created}{" "}
                          created)
                        </h3>
                        <div className="bg-slate-950/50 p-4 border border-slate-800 rounded">
                          <pre className="text-xs text-slate-300 overflow-x-auto max-h-96 overflow-y-auto">
                            {JSON.stringify(
                              debugInfo.backendResponse.cards,
                              null,
                              2
                            )}
                          </pre>
                        </div>
                      </div>
                    )}

                    {/* Backend Error */}
                    {debugInfo.backendError && (
                      <div>
                        <h3 className="text-red-400 font-semibold mb-3 uppercase text-sm tracking-wide flex items-center gap-2">
                          <div className="w-2 h-2 bg-red-500" />
                          Backend Error
                        </h3>
                        <div className="bg-red-950/30 p-4 border border-red-800 rounded">
                          <p className="text-red-400 text-sm">
                            {debugInfo.backendError}
                          </p>
                        </div>
                      </div>
                    )}

                    {/* OpenRouter Error */}
                    {debugInfo.openRouterError && (
                      <div>
                        <h3 className="text-red-400 font-semibold mb-3 uppercase text-sm tracking-wide flex items-center gap-2">
                          <div className="w-2 h-2 bg-red-500" />
                          OpenRouter Error
                        </h3>
                        <div className="bg-red-950/30 p-4 border border-red-800 rounded">
                          <pre className="text-red-400 text-xs overflow-x-auto">
                            {debugInfo.openRouterError}
                          </pre>
                        </div>
                      </div>
                    )}

                    {/* Parse Error */}
                    {debugInfo.parseError && (
                      <div>
                        <h3 className="text-red-400 font-semibold mb-3 uppercase text-sm tracking-wide flex items-center gap-2">
                          <div className="w-2 h-2 bg-red-500" />
                          Parse Error
                        </h3>
                        <div className="bg-red-950/30 p-4 border border-red-800 rounded">
                          <p className="text-red-400 text-sm">
                            {debugInfo.parseError}
                          </p>
                        </div>
                      </div>
                    )}
                  </div>
                )}
              </div>
            )}
          </div>
        </div>
      </div>
      <Footer />
    </div>
  );
}


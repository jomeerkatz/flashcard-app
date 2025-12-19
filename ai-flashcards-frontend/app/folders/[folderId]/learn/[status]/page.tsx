"use client";

import { Suspense } from "react";
import { useParams, useRouter } from "next/navigation";
import { useSession } from "next-auth/react";
import { useEffect, useState, useCallback } from "react";
import Header from "@/components/Header";
import Footer from "@/components/Footer";
import {
  getCardsForLearning,
  updateCardStatus,
  getAllFolders,
} from "@/lib/api-client";
import { CardDto, CardStatus } from "@/types/card";
import { FolderDto, PageResponse } from "@/types/folder";

type CardSetType = "first10" | "second10" | "all20";

// Fisher-Yates shuffle algorithm
function shuffleArray<T>(array: T[]): T[] {
  const shuffled = [...array];
  for (let i = shuffled.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
  }
  return shuffled;
}

export default function LearnCardsPage() {
  const params = useParams();
  const router = useRouter();
  const { data: session, status } = useSession();
  const folderId = params?.folderId as string;
  const statusParam = params?.status as string;

  const [allCards, setAllCards] = useState<CardDto[]>([]);
  const [first10Cards, setFirst10Cards] = useState<CardDto[]>([]);
  const [second10Cards, setSecond10Cards] = useState<CardDto[]>([]);
  const [currentCardSet, setCurrentCardSet] = useState<CardSetType>("first10");
  const [currentCardIndex, setCurrentCardIndex] = useState(0);
  const [isAnswerRevealed, setIsAnswerRevealed] = useState(false);
  const [learningStatus, setLearningStatus] = useState<CardStatus | null>(null);
  const [pageNumber, setPageNumber] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [folderName, setFolderName] = useState<string | null>(null);
  const [isUpdatingStatus, setIsUpdatingStatus] = useState(false);

  // Validate and set learning status from URL param
  useEffect(() => {
    if (statusParam) {
      const upperStatus = statusParam.toUpperCase();
      if (Object.values(CardStatus).includes(upperStatus as CardStatus)) {
        setLearningStatus(upperStatus as CardStatus);
      } else {
        setError("Invalid status parameter");
        setLoading(false);
      }
    }
  }, [statusParam]);

  // Fetch folder name
  useEffect(() => {
    const fetchFolderName = async () => {
      if (!session?.accessToken || !folderId) return;

      try {
        const folderIdNum = parseInt(folderId, 10);
        if (isNaN(folderIdNum)) return;

        const foldersData = await getAllFolders(session.accessToken, 0, 100);
        const folder = foldersData.content.find(
          (f: FolderDto) => f.id === folderIdNum
        );
        if (folder) {
          setFolderName(folder.name);
        }
      } catch (err) {
        console.error("Failed to fetch folder name:", err);
      }
    };

    if (status === "authenticated" && session?.accessToken && folderId) {
      fetchFolderName();
    }
  }, [status, session?.accessToken, folderId]);

  // Fetch cards for learning
  const fetchCards = useCallback(
    async (page: number = 0) => {
      if (!session?.accessToken || !folderId || !learningStatus) return;

      try {
        setLoading(true);
        setError(null);
        const folderIdNum = parseInt(folderId, 10);
        if (isNaN(folderIdNum)) {
          throw new Error("Invalid folder ID");
        }

        const data = await getCardsForLearning(
          session.accessToken,
          folderIdNum,
          learningStatus,
          page,
          20
        );

        if (data.content.length === 0) {
          setAllCards([]);
          setFirst10Cards([]);
          setSecond10Cards([]);
          setLoading(false);
          return;
        }

        // Shuffle the cards
        const shuffled = shuffleArray(data.content);

        // Split into first 10 and second 10
        const first10 = shuffled.slice(0, 10);
        const second10 = shuffled.slice(10, 20);

        // Shuffle each set
        const shuffledFirst10 = shuffleArray(first10);
        const shuffledSecond10 = shuffleArray(second10);
        const shuffledAll = shuffleArray(shuffled);

        setAllCards(shuffledAll);
        setFirst10Cards(shuffledFirst10);
        setSecond10Cards(shuffledSecond10);
        setCurrentCardIndex(0);
        setIsAnswerRevealed(false);
      } catch (err) {
        console.error("Failed to fetch cards:", err);
        setError(
          err && typeof err === "object" && "message" in err
            ? (err.message as string)
            : "Failed to load cards. Please try again."
        );
      } finally {
        setLoading(false);
      }
    },
    [session?.accessToken, folderId, learningStatus]
  );

  // Initial fetch
  useEffect(() => {
    if (
      status === "authenticated" &&
      session?.accessToken &&
      folderId &&
      learningStatus
    ) {
      fetchCards(0);
    } else if (status === "unauthenticated") {
      setLoading(false);
    }
  }, [status, session?.accessToken, folderId, learningStatus, fetchCards]);

  // Get current card based on current set and index
  const getCurrentCard = (): CardDto | null => {
    let currentSet: CardDto[] = [];

    switch (currentCardSet) {
      case "first10":
        currentSet = first10Cards;
        break;
      case "second10":
        currentSet = second10Cards;
        break;
      case "all20":
        currentSet = allCards;
        break;
    }

    if (currentSet.length === 0) return null;
    return currentSet[currentCardIndex] || null;
  };

  // Remove card from all sets
  const removeCardFromAllSets = useCallback((cardId: number) => {
    setFirst10Cards((prev) => prev.filter((card) => card.id !== cardId));
    setSecond10Cards((prev) => prev.filter((card) => card.id !== cardId));
    setAllCards((prev) => prev.filter((card) => card.id !== cardId));
  }, []);

  // Adjust index if it's out of bounds (e.g., after cards are removed)
  useEffect(() => {
    let currentSet: CardDto[] = [];

    switch (currentCardSet) {
      case "first10":
        currentSet = first10Cards;
        break;
      case "second10":
        currentSet = second10Cards;
        break;
      case "all20":
        currentSet = allCards;
        break;
    }

    if (currentSet.length > 0 && currentCardIndex >= currentSet.length) {
      // Index is out of bounds, reset to 0
      setCurrentCardIndex(0);
    }
  }, [currentCardSet, currentCardIndex, first10Cards, second10Cards, allCards]);

  // Move to next card in current set
  const moveToNextCard = useCallback(() => {
    let currentSet: CardDto[] = [];

    switch (currentCardSet) {
      case "first10":
        currentSet = first10Cards;
        break;
      case "second10":
        currentSet = second10Cards;
        break;
      case "all20":
        currentSet = allCards;
        break;
    }

    if (currentSet.length === 0) {
      // No cards left
      return;
    }

    // Move to next card, cycle back to start if at end
    setCurrentCardIndex((prevIndex) => {
      const nextIndex = (prevIndex + 1) % currentSet.length;
      return nextIndex;
    });
    setIsAnswerRevealed(false);
  }, [currentCardSet, first10Cards, second10Cards, allCards]);

  // Handle status selection
  const handleStatusSelect = async (selectedStatus: CardStatus) => {
    const currentCard = getCurrentCard();
    if (!currentCard || !session?.accessToken || !folderId || isUpdatingStatus)
      return;

    const folderIdNum = parseInt(folderId, 10);
    if (isNaN(folderIdNum)) return;

    setIsUpdatingStatus(true);

    try {
      // Update status in backend (fire and forget)
      updateCardStatus(
        session.accessToken,
        folderIdNum,
        currentCard.id,
        selectedStatus
      ).catch((err) => {
        console.error("Failed to update card status:", err);
        // Continue even if update fails
      });

      // If status changed from learning status, remove card
      if (selectedStatus !== learningStatus) {
        removeCardFromAllSets(currentCard.id);
      }

      // Move to next card
      moveToNextCard();
    } catch (err) {
      console.error("Error handling status selection:", err);
    } finally {
      setIsUpdatingStatus(false);
    }
  };

  // Switch to different card set
  const switchCardSet = (newSet: CardSetType) => {
    setCurrentCardSet(newSet);
    setCurrentCardIndex(0);
    setIsAnswerRevealed(false);

    // Shuffle the set when switching
    if (newSet === "first10" && first10Cards.length > 0) {
      setFirst10Cards(shuffleArray(first10Cards));
    } else if (newSet === "second10" && second10Cards.length > 0) {
      setSecond10Cards(shuffleArray(second10Cards));
    } else if (newSet === "all20" && allCards.length > 0) {
      setAllCards(shuffleArray(allCards));
    }
  };

  // Fetch next 20 cards
  const handleFetchNext20 = async () => {
    const nextPage = pageNumber + 1;
    setPageNumber(nextPage);
    await fetchCards(nextPage);
    setCurrentCardSet("first10");
  };

  if (status === "loading") {
    return (
      <div className="flex flex-col min-h-screen">
        <Header />
        <div className="flex-1 flex items-center justify-center py-20 px-4">
          <div className="text-center">
            <div className="w-16 h-16 border-4 border-orange-500 border-t-transparent animate-spin mx-auto mb-4" />
            <p className="text-slate-400">Loading...</p>
          </div>
        </div>
        <Footer />
      </div>
    );
  }

  if (status === "unauthenticated") {
    return (
      <div className="flex flex-col min-h-screen">
        <Header />
        <div className="flex-1 flex items-center justify-center py-20 px-4">
          <div className="text-center">
            <p className="text-slate-400">Please sign in to learn cards.</p>
          </div>
        </div>
        <Footer />
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex flex-col min-h-screen">
        <Header />
        <div className="flex-1 flex items-center justify-center py-20 px-4">
          <div className="text-center max-w-2xl mx-auto">
            <div className="bg-red-900/30 border-2 border-red-600 p-6 mb-4">
              <p className="text-red-400 font-semibold">{error}</p>
            </div>
            <button
              onClick={() => router.push(`/folders/${folderId}`)}
              className="px-6 py-2 bg-transparent border-2 border-orange-500 text-orange-500 hover:bg-orange-500 hover:text-black font-semibold transition-all duration-200"
            >
              Back to Cards
            </button>
          </div>
        </div>
        <Footer />
      </div>
    );
  }

  const currentCard = getCurrentCard();
  let currentSetLength = 0;
  switch (currentCardSet) {
    case "first10":
      currentSetLength = first10Cards.length;
      break;
    case "second10":
      currentSetLength = second10Cards.length;
      break;
    case "all20":
      currentSetLength = allCards.length;
      break;
  }

  return (
    <div className="flex flex-col min-h-screen">
      <Header />
      <Suspense fallback={<div className="flex-1" />}>
        <div className="flex-1 py-12 px-4">
          <div className="container mx-auto max-w-4xl">
            <button
              onClick={() => router.push(`/folders/${folderId}`)}
              className="mb-4 flex items-center gap-2 text-orange-500 hover:text-orange-400 transition-colors focus:outline-none focus:ring-2 focus:ring-orange-500 focus:ring-offset-2 focus:ring-offset-black"
              aria-label="Back to cards"
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
                Back to Cards
              </span>
            </button>

            <div className="mb-8">
              <h1 className="text-3xl sm:text-4xl font-bold text-white mb-2">
                {folderName ? `Learning: ${folderName}` : "Learning Cards"}
              </h1>
              <p className="text-slate-400">
                Status:{" "}
                <span className="font-semibold text-white">
                  {learningStatus}
                </span>
              </p>
            </div>

            {loading ? (
              <div className="flex items-center justify-center py-20">
                <div className="text-center">
                  <div className="w-16 h-16 border-4 border-orange-500 border-t-transparent animate-spin mx-auto mb-4" />
                  <p className="text-slate-400">Loading cards...</p>
                </div>
              </div>
            ) : currentSetLength === 0 ? (
              <div className="flex items-center justify-center py-20">
                <div className="text-center">
                  <p className="text-slate-400 text-lg mb-4">
                    {allCards.length === 0
                      ? "No cards available for this status."
                      : "All cards in this set have been learned."}
                  </p>
                  <div className="flex flex-wrap gap-4 justify-center">
                    {currentCardSet === "first10" &&
                      second10Cards.length > 0 && (
                        <button
                          onClick={() => switchCardSet("second10")}
                          className="px-6 py-2 bg-transparent border-2 border-orange-500 text-orange-500 hover:bg-orange-500 hover:text-black font-semibold transition-all duration-200"
                        >
                          Learn Next 10
                        </button>
                      )}
                    {currentCardSet === "second10" && allCards.length > 0 && (
                      <button
                        onClick={() => switchCardSet("all20")}
                        className="px-6 py-2 bg-transparent border-2 border-orange-500 text-orange-500 hover:bg-orange-500 hover:text-black font-semibold transition-all duration-200"
                      >
                        Learn All 20
                      </button>
                    )}
                    <button
                      onClick={handleFetchNext20}
                      className="px-6 py-2 bg-transparent border-2 border-orange-500 text-orange-500 hover:bg-orange-500 hover:text-black font-semibold transition-all duration-200"
                    >
                      Fetch Next 20
                    </button>
                    <button
                      onClick={() => router.push(`/folders/${folderId}`)}
                      className="px-6 py-2 bg-transparent border-2 border-slate-500 text-slate-400 hover:bg-slate-500 hover:text-white font-semibold transition-all duration-200"
                    >
                      Back to Overview
                    </button>
                  </div>
                </div>
              </div>
            ) : currentCard ? (
              <div className="space-y-6">
                {/* Card Display */}
                <div className="bg-slate-900 border-2 border-slate-800 p-8 rounded-lg">
                  <div className="mb-6">
                    <p className="text-slate-400 text-sm mb-2">
                      Card {currentCardIndex + 1} of {currentSetLength}
                    </p>
                    <h2 className="text-2xl font-bold text-white mb-4">
                      Question
                    </h2>
                    <p className="text-lg text-slate-200">
                      {currentCard.question}
                    </p>
                  </div>

                  {!isAnswerRevealed ? (
                    <button
                      onClick={() => setIsAnswerRevealed(true)}
                      className="w-full px-6 py-3 bg-transparent border-2 border-orange-500 text-orange-500 hover:bg-orange-500 hover:text-black font-semibold transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-orange-500 focus:ring-offset-2 focus:ring-offset-black"
                    >
                      Reveal Answer
                    </button>
                  ) : (
                    <div className="space-y-6">
                      <div>
                        <h3 className="text-xl font-bold text-white mb-3">
                          Answer
                        </h3>
                        <p className="text-lg text-slate-200 mb-6">
                          {currentCard.answer}
                        </p>
                      </div>

                      <div>
                        <p className="text-slate-400 text-sm mb-3">
                          How well did you know this?
                        </p>
                        <div className="flex flex-wrap gap-4">
                          <button
                            onClick={() => handleStatusSelect(CardStatus.GOOD)}
                            disabled={isUpdatingStatus}
                            className="px-6 py-3 bg-green-900/30 border-2 border-green-600 text-green-400 hover:bg-green-900/50 font-semibold transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-offset-2 focus:ring-offset-black disabled:opacity-50 disabled:cursor-not-allowed"
                          >
                            Good
                          </button>
                          <button
                            onClick={() =>
                              handleStatusSelect(CardStatus.MEDIUM)
                            }
                            disabled={isUpdatingStatus}
                            className="px-6 py-3 bg-yellow-900/30 border-2 border-yellow-600 text-yellow-400 hover:bg-yellow-900/50 font-semibold transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-yellow-500 focus:ring-offset-2 focus:ring-offset-black disabled:opacity-50 disabled:cursor-not-allowed"
                          >
                            Medium
                          </button>
                          <button
                            onClick={() => handleStatusSelect(CardStatus.BAD)}
                            disabled={isUpdatingStatus}
                            className="px-6 py-3 bg-red-900/30 border-2 border-red-600 text-red-400 hover:bg-red-900/50 font-semibold transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2 focus:ring-offset-black disabled:opacity-50 disabled:cursor-not-allowed"
                          >
                            Bad
                          </button>
                        </div>
                      </div>
                    </div>
                  )}
                </div>

                {/* Navigation Buttons */}
                <div className="flex flex-wrap gap-4 justify-center">
                  {currentCardSet === "first10" && second10Cards.length > 0 && (
                    <button
                      onClick={() => switchCardSet("second10")}
                      className="px-6 py-2 bg-transparent border-2 border-orange-500 text-orange-500 hover:bg-orange-500 hover:text-black font-semibold transition-all duration-200"
                    >
                      Get Next 10
                    </button>
                  )}
                  {currentCardSet === "second10" && allCards.length > 0 && (
                    <button
                      onClick={() => switchCardSet("all20")}
                      className="px-6 py-2 bg-transparent border-2 border-orange-500 text-orange-500 hover:bg-orange-500 hover:text-black font-semibold transition-all duration-200"
                    >
                      Learn All 20
                    </button>
                  )}
                  <button
                    onClick={handleFetchNext20}
                    className="px-6 py-2 bg-transparent border-2 border-orange-500 text-orange-500 hover:bg-orange-500 hover:text-black font-semibold transition-all duration-200"
                  >
                    Fetch Next 20
                  </button>
                </div>
              </div>
            ) : null}
          </div>
        </div>
      </Suspense>
      <Footer />
    </div>
  );
}

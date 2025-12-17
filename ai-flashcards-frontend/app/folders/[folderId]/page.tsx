"use client";

import { Suspense } from "react";
import { useParams } from "next/navigation";
import { useSession } from "next-auth/react";
import { useEffect, useState } from "react";
import Header from "@/components/Header";
import Footer from "@/components/Footer";
import Cards from "@/components/Cards";
import { getAllCardsOfFolder } from "@/lib/api-client";
import { CardDto } from "@/types/card";
import { PageResponse } from "@/types/folder";

export default function FolderCardsPage() {
  const params = useParams();
  const { data: session, status } = useSession();
  const folderId = params?.folderId as string;
  const [cards, setCards] = useState<PageResponse<CardDto> | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (status === "authenticated" && session?.accessToken && folderId) {
      fetchCards();
    } else if (status === "unauthenticated") {
      setLoading(false);
    }
  }, [status, session?.accessToken, folderId]);

  const fetchCards = async () => {
    if (!session?.accessToken || !folderId) return;

    try {
      setLoading(true);
      setError(null);
      const folderIdNum = parseInt(folderId, 10);
      if (isNaN(folderIdNum)) {
        throw new Error("Invalid folder ID");
      }
      const data = await getAllCardsOfFolder(
        session.accessToken,
        folderIdNum,
        0,
        10
      );
      setCards(data);
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
            <p className="text-slate-400">Please sign in to view cards.</p>
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
              onClick={fetchCards}
              className="px-6 py-2 bg-transparent border-2 border-orange-500 text-orange-500 hover:bg-orange-500 hover:text-black font-semibold transition-all duration-200"
            >
              Try Again
            </button>
          </div>
        </div>
        <Footer />
      </div>
    );
  }

  return (
    <div className="flex flex-col min-h-screen">
      <Header />
      <Suspense fallback={<div className="flex-1" />}>
        <div className="flex-1 py-12 px-4">
          <div className="container mx-auto max-w-6xl">
            <h1 className="text-3xl sm:text-4xl font-bold text-white mb-8">
              Cards
            </h1>
            <Cards
              cards={cards?.content || []}
              loading={loading}
            />
          </div>
        </div>
      </Suspense>
      <Footer />
    </div>
  );
}


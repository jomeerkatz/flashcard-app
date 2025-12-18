"use client";

import { Suspense } from "react";
import { useParams, useRouter } from "next/navigation";
import { useSession } from "next-auth/react";
import { useEffect, useState } from "react";
import Header from "@/components/Header";
import Footer from "@/components/Footer";
import Cards from "@/components/Cards";
import CreateCardModal from "@/components/CreateCardModal";
import EditCardModal from "@/components/EditCardModal";
import DeleteCardModal from "@/components/DeleteCardModal";
import { getAllCardsOfFolder, getAllFolders } from "@/lib/api-client";
import { CardDto } from "@/types/card";
import { FolderDto, PageResponse } from "@/types/folder";

export default function FolderCardsPage() {
  const params = useParams();
  const router = useRouter();
  const { data: session, status } = useSession();
  const folderId = params?.folderId as string;
  const [cards, setCards] = useState<PageResponse<CardDto> | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingCard, setEditingCard] = useState<CardDto | null>(null);
  const [deletingCard, setDeletingCard] = useState<CardDto | null>(null);
  const [folderName, setFolderName] = useState<string | null>(null);

  useEffect(() => {
    if (status === "authenticated" && session?.accessToken && folderId) {
      fetchCards();
      fetchFolderName();
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

  const fetchFolderName = async () => {
    if (!session?.accessToken || !folderId) return;

    try {
      const folderIdNum = parseInt(folderId, 10);
      if (isNaN(folderIdNum)) {
        return;
      }
      // Fetch all folders and find the one matching the folderId
      // We'll fetch a large page size to ensure we get the folder
      const foldersData = await getAllFolders(session.accessToken, 0, 100);
      const folder = foldersData.content.find(
        (f: FolderDto) => f.id === folderIdNum
      );
      if (folder) {
        setFolderName(folder.name);
      }
    } catch (err) {
      console.error("Failed to fetch folder name:", err);
      // Don't set error state here, just log it
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

  const folderIdNum = folderId ? parseInt(folderId, 10) : null;

  return (
    <div className="flex flex-col min-h-screen">
      <Header />
      <Suspense fallback={<div className="flex-1" />}>
        <div className="flex-1 py-12 px-4">
          <div className="container mx-auto max-w-6xl">
            <button
              onClick={() => router.push("/folders")}
              className="mb-4 flex items-center gap-2 text-orange-500 hover:text-orange-400 transition-colors focus:outline-none focus:ring-2 focus:ring-orange-500 focus:ring-offset-2 focus:ring-offset-black"
              aria-label="Back to folders"
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
                Back to Folders
              </span>
            </button>
            <div className="flex items-center justify-between mb-8">
              <h1 className="text-3xl sm:text-4xl font-bold text-white">
                {folderName ? `Cards for Folder: ${folderName}` : "Cards"}
              </h1>
              <button
                onClick={() => setIsModalOpen(true)}
                className="px-6 py-2.5 bg-transparent border-2 border-orange-500 text-orange-500 hover:bg-orange-500 hover:text-black font-semibold transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-orange-500 focus:ring-offset-2 focus:ring-offset-black uppercase tracking-wide text-sm flex items-center gap-2"
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
                    d="M12 4v16m8-8H4"
                  />
                </svg>
                Create Card
              </button>
            </div>
            <Cards
              cards={cards?.content || []}
              loading={loading}
              onEdit={(card) => setEditingCard(card)}
              onDelete={(card) => setDeletingCard(card)}
            />
          </div>
        </div>
      </Suspense>
      <Footer />
      {session?.accessToken && folderIdNum && !isNaN(folderIdNum) && (
        <>
          <CreateCardModal
            isOpen={isModalOpen}
            onClose={() => setIsModalOpen(false)}
            onSuccess={fetchCards}
            folderId={folderIdNum}
            accessToken={session.accessToken}
          />
          {editingCard && (
            <EditCardModal
              isOpen={!!editingCard}
              onClose={() => setEditingCard(null)}
              onSuccess={fetchCards}
              folderId={folderIdNum}
              cardId={editingCard.id}
              accessToken={session.accessToken}
              card={editingCard}
            />
          )}
          {deletingCard && (
            <DeleteCardModal
              isOpen={!!deletingCard}
              onClose={() => setDeletingCard(null)}
              onSuccess={fetchCards}
              folderId={folderIdNum}
              cardId={deletingCard.id}
              accessToken={session.accessToken}
              card={deletingCard}
            />
          )}
        </>
      )}
    </div>
  );
}

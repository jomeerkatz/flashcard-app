"use client";

import { useSession } from "next-auth/react";
import { useEffect, useState } from "react";
import { getAllFolders } from "@/lib/api-client";
import { FolderDto, PageResponse } from "@/types/folder";

export default function Folders() {
  const { data: session, status } = useSession();
  const [folders, setFolders] = useState<PageResponse<FolderDto> | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const pageSize = 5;

  useEffect(() => {
    if (status === "authenticated" && session?.accessToken) {
      fetchFolders(currentPage);
    } else if (status === "unauthenticated") {
      setLoading(false);
    }
  }, [status, session?.accessToken, currentPage]);

  const fetchFolders = async (page: number) => {
    if (!session?.accessToken) return;

    try {
      setLoading(true);
      setError(null);
      const data = await getAllFolders(session.accessToken, page, pageSize);
      setFolders(data);
    } catch (err) {
      console.error("Failed to fetch folders:", err);
      setError(
        err && typeof err === "object" && "message" in err
          ? (err.message as string)
          : "Failed to load folders. Please try again."
      );
    } finally {
      setLoading(false);
    }
  };

  const handlePreviousPage = () => {
    if (folders && !folders.first) {
      setCurrentPage((prev) => prev - 1);
    }
  };

  const handleNextPage = () => {
    if (folders && !folders.last) {
      setCurrentPage((prev) => prev + 1);
    }
  };

  if (status === "loading") {
    return (
      <div className="flex-1 flex items-center justify-center py-20 px-4">
        <div className="text-center">
          <div className="w-16 h-16 border-4 border-orange-500 border-t-transparent animate-spin mx-auto mb-4" />
          <p className="text-slate-400">Loading...</p>
        </div>
      </div>
    );
  }

  if (status === "unauthenticated") {
    return (
      <div className="flex-1 flex items-center justify-center py-20 px-4">
        <div className="text-center">
          <p className="text-slate-400">Please sign in to view your folders.</p>
        </div>
      </div>
    );
  }

  if (loading) {
    return (
      <div className="flex-1 flex items-center justify-center py-20 px-4">
        <div className="text-center">
          <div className="w-16 h-16 border-4 border-orange-500 border-t-transparent animate-spin mx-auto mb-4" />
          <p className="text-slate-400">Loading folders...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex-1 flex items-center justify-center py-20 px-4">
        <div className="text-center max-w-2xl mx-auto">
          <div className="bg-red-900/30 border-2 border-red-600 p-6 mb-4">
            <p className="text-red-400 font-semibold">{error}</p>
          </div>
          <button
            onClick={() => fetchFolders(currentPage)}
            className="px-6 py-2 bg-transparent border-2 border-orange-500 text-orange-500 hover:bg-orange-500 hover:text-black font-semibold transition-all duration-200"
          >
            Try Again
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="flex-1 py-12 px-4">
      <div className="container mx-auto max-w-4xl">
        {/* Header with Create Button */}
        <div className="flex items-center justify-between mb-8">
          <h1 className="text-3xl sm:text-4xl font-bold text-white">
            My Folders
          </h1>
          <button
            className="px-6 py-2.5 bg-transparent border-2 border-orange-500 text-orange-500 hover:bg-orange-500 hover:text-black font-semibold transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-orange-500 focus:ring-offset-2 focus:ring-offset-black uppercase tracking-wide text-sm"
            aria-label="Create new folder"
          >
            Create New Folder
          </button>
        </div>

        {/* Folders Grid */}
        {folders && folders.content.length > 0 ? (
          <>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 mb-8">
              {folders.content.map((folder) => (
                <button
                  key={folder.id}
                  className="px-6 py-8 bg-slate-900 border-2 border-slate-800 hover:border-orange-500 text-white text-left transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-orange-500 focus:ring-offset-2 focus:ring-offset-black"
                  aria-label={`Folder: ${folder.name}`}
                >
                  <h2 className="text-xl font-semibold">{folder.name}</h2>
                </button>
              ))}
            </div>

            {/* Pagination Controls */}
            {folders.totalPages > 1 && (
              <div className="flex items-center justify-center gap-4">
                <button
                  onClick={handlePreviousPage}
                  disabled={folders.first}
                  className="px-6 py-2 bg-transparent border-2 border-orange-500 text-orange-500 hover:bg-orange-500 hover:text-black disabled:border-slate-800 disabled:text-slate-600 disabled:cursor-not-allowed disabled:hover:bg-transparent disabled:hover:text-slate-600 font-semibold transition-all duration-200 uppercase tracking-wide text-sm"
                  aria-label="Previous page"
                >
                  Previous
                </button>
                <span className="text-slate-400 font-medium">
                  Page {folders.number + 1} of {folders.totalPages}
                </span>
                <button
                  onClick={handleNextPage}
                  disabled={folders.last}
                  className="px-6 py-2 bg-transparent border-2 border-orange-500 text-orange-500 hover:bg-orange-500 hover:text-black disabled:border-slate-800 disabled:text-slate-600 disabled:cursor-not-allowed disabled:hover:bg-transparent disabled:hover:text-slate-600 font-semibold transition-all duration-200 uppercase tracking-wide text-sm"
                  aria-label="Next page"
                >
                  Next
                </button>
              </div>
            )}
          </>
        ) : (
          <div className="text-center py-20">
            <p className="text-slate-400 text-lg mb-6">
              You don't have any folders yet.
            </p>
            <button
              className="px-6 py-2.5 bg-transparent border-2 border-orange-500 text-orange-500 hover:bg-orange-500 hover:text-black font-semibold transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-orange-500 focus:ring-offset-2 focus:ring-offset-black uppercase tracking-wide text-sm"
              aria-label="Create new folder"
            >
              Create Your First Folder
            </button>
          </div>
        )}
      </div>
    </div>
  );
}

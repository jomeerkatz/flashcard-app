"use client";

import { useSession } from "next-auth/react";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { getAllFolders, createFolder } from "@/lib/api-client";
import { FolderDto, PageResponse } from "@/types/folder";

export default function Folders() {
  const { data: session, status } = useSession();
  const router = useRouter();
  const [folders, setFolders] = useState<PageResponse<FolderDto> | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const pageSize = 5;
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [folderName, setFolderName] = useState("");
  const [isCreating, setIsCreating] = useState(false);
  const [createError, setCreateError] = useState<string | null>(null);

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

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setFolderName("");
    setCreateError(null);
  };

  const handleCreateFolder = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!session?.accessToken) return;
    
    // Validate folder name is not empty
    if (!folderName.trim()) {
      setCreateError("Folder name cannot be blank");
      return;
    }

    try {
      setIsCreating(true);
      setCreateError(null);
      
      await createFolder(session.accessToken, folderName.trim());
      
      // Refresh folders list
      await fetchFolders(currentPage);
      
      // Close modal and reset form
      handleCloseModal();
    } catch (err) {
      console.error("Failed to create folder:", err);
      setCreateError(
        err && typeof err === "object" && "message" in err
          ? (err.message as string)
          : "Failed to create folder. Please try again."
      );
    } finally {
      setIsCreating(false);
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
            onClick={() => setIsModalOpen(true)}
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
                  onClick={() => router.push(`/folders/${folder.id}`)}
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
          </div>
        )}
      </div>

      {/* Create Folder Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center">
          {/* Backdrop */}
          <div className="fixed inset-0 bg-black/80" />
          
          {/* Modal Container */}
          <div className="relative bg-black border-2 border-orange-500 max-w-md w-full mx-4 shadow-xl">
            {/* Modal Header */}
            <div className="flex items-center justify-between p-6 border-b-2 border-slate-900">
              <h2 className="text-2xl font-bold text-white uppercase tracking-wide">
                Create New Folder
              </h2>
              <button
                onClick={handleCloseModal}
                className="text-slate-400 hover:text-orange-500 transition-colors focus:outline-none focus:ring-2 focus:ring-orange-500 focus:ring-offset-2 focus:ring-offset-black p-1"
                aria-label="Close modal"
                disabled={isCreating}
              >
                <svg
                  className="w-6 h-6"
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
              </button>
            </div>

            {/* Modal Body */}
            <form onSubmit={handleCreateFolder} className="p-6">
              <div className="mb-6">
                <label
                  htmlFor="folder-name"
                  className="block text-sm font-semibold text-white uppercase tracking-wide mb-2"
                >
                  Folder Name
                </label>
                <input
                  id="folder-name"
                  type="text"
                  value={folderName}
                  onChange={(e) => {
                    setFolderName(e.target.value);
                    setCreateError(null);
                  }}
                  placeholder="Enter folder name"
                  className="w-full px-4 py-3 bg-slate-900 border-2 border-slate-800 text-white placeholder-slate-500 focus:outline-none focus:border-orange-500 focus:ring-2 focus:ring-orange-500 focus:ring-offset-2 focus:ring-offset-black transition-all duration-200"
                  disabled={isCreating}
                  autoFocus
                />
                {createError && (
                  <p className="mt-2 text-sm text-red-400 font-semibold">
                    {createError}
                  </p>
                )}
              </div>

              {/* Modal Footer */}
              <div className="flex items-center justify-end gap-4">
                <button
                  type="button"
                  onClick={handleCloseModal}
                  disabled={isCreating}
                  className="px-6 py-2.5 bg-transparent border-2 border-orange-500 text-orange-500 hover:bg-orange-500 hover:text-black disabled:border-slate-800 disabled:text-slate-600 disabled:cursor-not-allowed disabled:hover:bg-transparent disabled:hover:text-slate-600 font-semibold transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-orange-500 focus:ring-offset-2 focus:ring-offset-black uppercase tracking-wide text-sm"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={!folderName.trim() || isCreating}
                  className="px-6 py-2.5 bg-transparent border-2 border-orange-500 text-orange-500 hover:bg-orange-500 hover:text-black disabled:border-slate-800 disabled:text-slate-600 disabled:cursor-not-allowed disabled:hover:bg-transparent disabled:hover:text-slate-600 font-semibold transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-orange-500 focus:ring-offset-2 focus:ring-offset-black uppercase tracking-wide text-sm flex items-center gap-2"
                >
                  {isCreating ? (
                    <>
                      <div className="w-4 h-4 border-2 border-orange-500 border-t-transparent animate-spin" />
                      Creating...
                    </>
                  ) : (
                    "Create"
                  )}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

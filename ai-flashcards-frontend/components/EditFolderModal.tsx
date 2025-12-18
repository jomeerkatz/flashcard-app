"use client";

import { useState, useEffect } from "react";
import { updateFolder } from "@/lib/api-client";
import { FolderDto } from "@/types/folder";

interface EditFolderModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
  folderId: number;
  accessToken: string;
  folder: FolderDto;
}

export default function EditFolderModal({
  isOpen,
  onClose,
  onSuccess,
  folderId,
  accessToken,
  folder,
}: EditFolderModalProps) {
  const [folderName, setFolderName] = useState(folder.name);
  const [isUpdating, setIsUpdating] = useState(false);
  const [updateError, setUpdateError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  // Update form field when folder prop changes
  useEffect(() => {
    if (folder) {
      setFolderName(folder.name);
    }
  }, [folder]);

  const handleCloseModal = () => {
    setFolderName(folder.name);
    setUpdateError(null);
    setSuccessMessage(null);
    onClose();
  };

  const handleUpdateFolder = async (e: React.FormEvent) => {
    e.preventDefault();

    // Validate folder name is not empty
    if (!folderName.trim()) {
      setUpdateError("Folder name cannot be blank");
      return;
    }

    try {
      setIsUpdating(true);
      setUpdateError(null);

      await updateFolder(accessToken, folderId, folderName.trim());

      // Show success message
      setSuccessMessage("Folder updated successfully!");

      // Wait a moment to show success message, then close and refresh
      setTimeout(() => {
        handleCloseModal();
        onSuccess();
      }, 1500);
    } catch (err) {
      console.error("Failed to update folder:", err);
      setUpdateError(
        err && typeof err === "object" && "message" in err
          ? (err.message as string)
          : "Failed to update folder. Please try again."
      );
    } finally {
      setIsUpdating(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      {/* Backdrop */}
      <div className="fixed inset-0 bg-black/80" />

      {/* Modal Container */}
      <div className="relative bg-black border-2 border-orange-500 max-w-md w-full mx-4 shadow-xl">
        {/* Modal Header */}
        <div className="flex items-center justify-between p-6 border-b-2 border-slate-900">
          <h2 className="text-2xl font-bold text-white uppercase tracking-wide">
            Edit Folder
          </h2>
          <button
            onClick={handleCloseModal}
            className="text-slate-400 hover:text-orange-500 transition-colors focus:outline-none focus:ring-2 focus:ring-orange-500 focus:ring-offset-2 focus:ring-offset-black p-1"
            aria-label="Close modal"
            disabled={isUpdating}
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
        <form onSubmit={handleUpdateFolder} className="p-6">
          {/* Success Message */}
          {successMessage && (
            <div className="mb-6 bg-green-900/30 border-2 border-green-600 p-4">
              <p className="text-green-400 font-semibold">{successMessage}</p>
            </div>
          )}

          {/* Folder Name Field */}
          <div className="mb-6">
            <label
              htmlFor="edit-folder-name"
              className="block text-sm font-semibold text-white uppercase tracking-wide mb-2"
            >
              Folder Name
            </label>
            <input
              id="edit-folder-name"
              type="text"
              value={folderName}
              onChange={(e) => {
                setFolderName(e.target.value);
                setUpdateError(null);
              }}
              placeholder="Enter folder name"
              className="w-full px-4 py-3 bg-slate-900 border-2 border-slate-800 text-white placeholder-slate-500 focus:outline-none focus:border-orange-500 focus:ring-2 focus:ring-orange-500 focus:ring-offset-2 focus:ring-offset-black transition-all duration-200"
              disabled={isUpdating}
              autoFocus
            />
            {updateError && (
              <p className="mt-2 text-sm text-red-400 font-semibold">
                {updateError}
              </p>
            )}
          </div>

          {/* Modal Footer */}
          <div className="flex items-center justify-end gap-4">
            <button
              type="button"
              onClick={handleCloseModal}
              disabled={isUpdating}
              className="px-6 py-2.5 bg-transparent border-2 border-orange-500 text-orange-500 hover:bg-orange-500 hover:text-black disabled:border-slate-800 disabled:text-slate-600 disabled:cursor-not-allowed disabled:hover:bg-transparent disabled:hover:text-slate-600 font-semibold transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-orange-500 focus:ring-offset-2 focus:ring-offset-black uppercase tracking-wide text-sm"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={!folderName.trim() || isUpdating}
              className="px-6 py-2.5 bg-transparent border-2 border-orange-500 text-orange-500 hover:bg-orange-500 hover:text-black disabled:border-slate-800 disabled:text-slate-600 disabled:cursor-not-allowed disabled:hover:bg-transparent disabled:hover:text-slate-600 font-semibold transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-orange-500 focus:ring-offset-2 focus:ring-offset-black uppercase tracking-wide text-sm flex items-center gap-2"
            >
              {isUpdating ? (
                <>
                  <div className="w-4 h-4 border-2 border-orange-500 border-t-transparent animate-spin" />
                  Updating...
                </>
              ) : (
                "Update"
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}


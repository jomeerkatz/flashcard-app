"use client";

import { useState } from "react";
import { createCard } from "@/lib/api-client";
import { CardDto } from "@/types/card";

interface CreateCardModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
  folderId: number;
  accessToken: string;
}

export default function CreateCardModal({
  isOpen,
  onClose,
  onSuccess,
  folderId,
  accessToken,
}: CreateCardModalProps) {
  const [question, setQuestion] = useState("");
  const [answer, setAnswer] = useState("");
  const [isCreating, setIsCreating] = useState(false);
  const [createError, setCreateError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const handleCloseModal = () => {
    setQuestion("");
    setAnswer("");
    setCreateError(null);
    setSuccessMessage(null);
    onClose();
  };

  const handleCreateCard = async (e: React.FormEvent) => {
    e.preventDefault();

    // Validate question and answer are not empty
    if (!question.trim() || !answer.trim()) {
      setCreateError("Question and answer cannot be blank");
      return;
    }

    try {
      setIsCreating(true);
      setCreateError(null);

      await createCard(accessToken, folderId, question.trim(), answer.trim());

      // Show success message
      setSuccessMessage("Card created successfully!");

      // Wait a moment to show success message, then close and refresh
      setTimeout(() => {
        handleCloseModal();
        onSuccess();
      }, 1500);
    } catch (err) {
      console.error("Failed to create card:", err);
      setCreateError(
        err && typeof err === "object" && "message" in err
          ? (err.message as string)
          : "Failed to create card. Please try again."
      );
    } finally {
      setIsCreating(false);
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
            Create New Card
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
        <form onSubmit={handleCreateCard} className="p-6">
          {/* Success Message */}
          {successMessage && (
            <div className="mb-6 bg-green-900/30 border-2 border-green-600 p-4">
              <p className="text-green-400 font-semibold">{successMessage}</p>
            </div>
          )}

          {/* Question Field */}
          <div className="mb-6">
            <label
              htmlFor="card-question"
              className="block text-sm font-semibold text-white uppercase tracking-wide mb-2"
            >
              Question
            </label>
            <textarea
              id="card-question"
              value={question}
              onChange={(e) => {
                setQuestion(e.target.value);
                setCreateError(null);
              }}
              placeholder="Enter question"
              rows={3}
              className="w-full px-4 py-3 bg-slate-900 border-2 border-slate-800 text-white placeholder-slate-500 focus:outline-none focus:border-orange-500 focus:ring-2 focus:ring-orange-500 focus:ring-offset-2 focus:ring-offset-black transition-all duration-200 resize-none"
              disabled={isCreating}
              autoFocus
            />
          </div>

          {/* Answer Field */}
          <div className="mb-6">
            <label
              htmlFor="card-answer"
              className="block text-sm font-semibold text-white uppercase tracking-wide mb-2"
            >
              Answer
            </label>
            <textarea
              id="card-answer"
              value={answer}
              onChange={(e) => {
                setAnswer(e.target.value);
                setCreateError(null);
              }}
              placeholder="Enter answer"
              rows={3}
              className="w-full px-4 py-3 bg-slate-900 border-2 border-slate-800 text-white placeholder-slate-500 focus:outline-none focus:border-orange-500 focus:ring-2 focus:ring-orange-500 focus:ring-offset-2 focus:ring-offset-black transition-all duration-200 resize-none"
              disabled={isCreating}
            />
          </div>

          {/* Error Message */}
          {createError && (
            <div className="mb-6 bg-red-900/30 border-2 border-red-600 p-4">
              <p className="text-red-400 font-semibold">{createError}</p>
            </div>
          )}

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
              disabled={!question.trim() || !answer.trim() || isCreating}
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
  );
}

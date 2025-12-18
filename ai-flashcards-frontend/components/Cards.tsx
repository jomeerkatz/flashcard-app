"use client";

import { CardDto, CardStatus } from "@/types/card";

interface CardsProps {
  cards: CardDto[];
  loading?: boolean;
  onEdit?: (card: CardDto) => void;
  onDelete?: (card: CardDto) => void;
}

export default function Cards({ cards, loading, onEdit, onDelete }: CardsProps) {
  if (loading) {
    return (
      <div className="flex items-center justify-center py-20 px-4">
        <div className="text-center">
          <div className="w-16 h-16 border-4 border-orange-500 border-t-transparent animate-spin mx-auto mb-4" />
          <p className="text-slate-400">Loading cards...</p>
        </div>
      </div>
    );
  }

  if (cards.length === 0) {
    return (
      <div className="flex items-center justify-center py-20 px-4">
        <div className="text-center">
          <p className="text-slate-400 text-lg">
            No cards found in this folder.
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="w-full">
      <div className="overflow-x-auto">
        <table className="w-full border-collapse">
          <thead>
            <tr className="border-b-2 border-slate-800">
              <th className="px-4 py-3 text-left text-sm font-semibold text-white uppercase tracking-wide">
                id
              </th>
              <th className="px-4 py-3 text-left text-sm font-semibold text-white uppercase tracking-wide">
                Question
              </th>
              <th className="px-4 py-3 text-left text-sm font-semibold text-white uppercase tracking-wide">
                Answer
              </th>
              <th className="px-4 py-3 text-left text-sm font-semibold text-white uppercase tracking-wide">
                cardstatus
              </th>
              {(onEdit || onDelete) && (
                <th className="px-4 py-3 text-left text-sm font-semibold text-white uppercase tracking-wide">
                  Actions
                </th>
              )}
            </tr>
          </thead>
          <tbody>
            {cards.map((card) => (
              <tr
                key={card.id}
                className="border-b border-slate-800 hover:bg-slate-900/50 transition-colors"
              >
                <td className="px-4 py-3 text-white">{card.id}</td>
                <td className="px-4 py-3 text-white">{card.question}</td>
                <td className="px-4 py-3 text-white">{card.answer}</td>
                <td className="px-4 py-3">
                  <span
                    className={`inline-block px-3 py-1 rounded text-sm font-semibold ${
                      card.status === CardStatus.GOOD
                        ? "bg-green-900/30 text-green-400 border border-green-600"
                        : card.status === CardStatus.MEDIUM
                        ? "bg-yellow-900/30 text-yellow-400 border border-yellow-600"
                        : "bg-red-900/30 text-red-400 border border-red-600"
                    }`}
                  >
                    {card.status}
                  </span>
                </td>
                {(onEdit || onDelete) && (
                  <td className="px-4 py-3">
                    <div className="flex items-center gap-3">
                      {onEdit && (
                        <button
                          onClick={() => onEdit(card)}
                          className="text-orange-500 hover:text-orange-400 transition-colors focus:outline-none focus:ring-2 focus:ring-orange-500 focus:ring-offset-2 focus:ring-offset-black p-1"
                          aria-label="Edit card"
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
                              d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"
                            />
                          </svg>
                        </button>
                      )}
                      {onDelete && (
                        <button
                          onClick={() => onDelete(card)}
                          className="text-red-500 hover:text-red-400 transition-colors focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2 focus:ring-offset-black p-1"
                          aria-label="Delete card"
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
                              d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                            />
                          </svg>
                        </button>
                      )}
                    </div>
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

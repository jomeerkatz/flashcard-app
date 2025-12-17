export enum CardStatus {
  GOOD = "GOOD",
  MEDIUM = "MEDIUM",
  BAD = "BAD",
}

export interface CardDto {
  id: number;
  question: string;
  answer: string;
  status: CardStatus;
  createdAt: string; // ISO date string from LocalDateTime
  updatedAt: string; // ISO date string from LocalDateTime
}


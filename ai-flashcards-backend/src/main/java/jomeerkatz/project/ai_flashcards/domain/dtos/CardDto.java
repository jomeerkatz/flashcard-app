package jomeerkatz.project.ai_flashcards.domain.dtos;

import jomeerkatz.project.ai_flashcards.domain.enums.CardStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardDto {
    private Long id;

    private String question;

    private String answer;

    private CardStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

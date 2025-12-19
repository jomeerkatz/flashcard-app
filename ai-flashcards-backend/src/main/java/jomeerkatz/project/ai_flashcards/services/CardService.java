package jomeerkatz.project.ai_flashcards.services;

import jomeerkatz.project.ai_flashcards.domain.CardCreateUpdateRequest;
import jomeerkatz.project.ai_flashcards.domain.entities.Card;
import jomeerkatz.project.ai_flashcards.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CardService {
    Page<Card> getAllCards(User user, Long folderId, Pageable pageable);
    Card createCard(User user, Long folderId, CardCreateUpdateRequest card);
    void updateCard(User user, Long folderId, CardCreateUpdateRequest card, Long cardId);
    void deleteCard(User user, Long folderId, Long cardId);
    Long getCountOfCardsByFoldeId(User user, Long folderId);
}

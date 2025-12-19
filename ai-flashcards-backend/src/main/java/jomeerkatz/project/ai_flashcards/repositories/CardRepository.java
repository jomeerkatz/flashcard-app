package jomeerkatz.project.ai_flashcards.repositories;

import jomeerkatz.project.ai_flashcards.domain.entities.Card;
import jomeerkatz.project.ai_flashcards.domain.enums.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Page<Card> findAllByUserIdAndFolderId(Long userId, Long folderId, Pageable pageable);
    Optional<Card> findByIdAndFolderId(Long id, Long folderId);
    void deleteAllByFolderId(Long folderId);
    Long countByFolderId(Long folderId);
    Page<Card> findAllByUserIdAndFolderIdAndStatus(Long userId, Long folderId, CardStatus status, Pageable pageable);
}

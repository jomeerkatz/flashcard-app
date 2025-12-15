package jomeerkatz.project.ai_flashcards.repositories;

import jomeerkatz.project.ai_flashcards.domain.entities.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    boolean existsByUserIdAndName(Long userId, String name);
}

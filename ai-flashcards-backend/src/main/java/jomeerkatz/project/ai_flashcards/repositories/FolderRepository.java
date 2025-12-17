package jomeerkatz.project.ai_flashcards.repositories;

import jomeerkatz.project.ai_flashcards.domain.entities.Folder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    boolean existsByUserIdAndName(Long userId, String name);
    Page<Folder> findAllByUserId(Long userId, Pageable pageable);
}

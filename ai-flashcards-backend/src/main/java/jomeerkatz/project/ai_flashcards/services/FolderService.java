package jomeerkatz.project.ai_flashcards.services;

import jomeerkatz.project.ai_flashcards.domain.FolderCreateUpdateRequest;
import jomeerkatz.project.ai_flashcards.domain.entities.Folder;
import jomeerkatz.project.ai_flashcards.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FolderService {
    Folder saveFolder(User user, FolderCreateUpdateRequest folderCreateUpdateRequest);
    Page<Folder> getAllFolders(User user, Pageable pageable);
    void updateFolder(User user, Long folderId, FolderCreateUpdateRequest folderCreateUpdateRequest);
}

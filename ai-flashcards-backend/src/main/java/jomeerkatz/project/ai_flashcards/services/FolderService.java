package jomeerkatz.project.ai_flashcards.services;

import jomeerkatz.project.ai_flashcards.domain.FolderCreateUpdateRequest;
import jomeerkatz.project.ai_flashcards.domain.dtos.FolderDto;
import jomeerkatz.project.ai_flashcards.domain.entities.User;

public interface FolderService {
    FolderDto createFolder(User user, FolderCreateUpdateRequest folderCreateUpdateRequest);
}

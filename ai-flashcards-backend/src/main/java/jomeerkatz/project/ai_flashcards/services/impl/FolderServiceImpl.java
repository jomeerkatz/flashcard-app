package jomeerkatz.project.ai_flashcards.services.impl;

import jakarta.transaction.Transactional;
import jomeerkatz.project.ai_flashcards.domain.FolderCreateUpdateRequest;
import jomeerkatz.project.ai_flashcards.domain.dtos.FolderDto;
import jomeerkatz.project.ai_flashcards.domain.entities.Folder;
import jomeerkatz.project.ai_flashcards.domain.entities.User;
import jomeerkatz.project.ai_flashcards.exceptions.*;
import jomeerkatz.project.ai_flashcards.exceptions.FolderAlreadyExistsException;
import jomeerkatz.project.ai_flashcards.mappers.FolderMapper;
import jomeerkatz.project.ai_flashcards.repositories.CardRepository;
import jomeerkatz.project.ai_flashcards.repositories.FolderRepository;
import jomeerkatz.project.ai_flashcards.repositories.UserRepository;
import jomeerkatz.project.ai_flashcards.services.FolderService;
import jomeerkatz.project.ai_flashcards.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class FolderServiceImpl implements FolderService {

    private final FolderRepository folderRepository;
    private final UserService userService;
    private final CardRepository cardRepository;

    @Override
    @Transactional
    public Folder saveFolder(User user, FolderCreateUpdateRequest folderCreateUpdateRequest) {
        User savedUser = userService.getUserOrThrow(user);

        boolean folderExistsForUser = folderRepository
                .existsByUserIdAndName(savedUser.getId(), folderCreateUpdateRequest.getName());

        if (!folderExistsForUser) {
            return folderRepository.save(Folder.builder()
                    .name(folderCreateUpdateRequest.getName())
                    .user(savedUser)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build());
        } else {
            throw new FolderAlreadyExistsException("folder with name " + folderCreateUpdateRequest.getName() + " already exists!");
        }
    }

    @Override
    public Page<Folder> getAllFolders(User user, Pageable pageable) {
        User savedUser = userService.getUserOrThrow(user);
        return folderRepository.findAllByUserId(savedUser.getId(), pageable);
    }

    @Override
    @Transactional
    public void updateFolder(User user, Long folderId, FolderCreateUpdateRequest folderCreateUpdateRequest) {
        User savedUser = userService.getUserOrThrow(user);

        Folder folderToBeUpdated = folderRepository.findById(folderId).orElseThrow(
                () -> new FolderDoesNotExists("folder does not exists by id " + folderId)
        );

        boolean folderExistsForUser = folderRepository
                .existsByUserIdAndName(savedUser.getId(), folderToBeUpdated.getName());

        if (!folderExistsForUser) {
            // folder does not exist in combination of user and folder
            throw new FolderAccessDeniedException("User has no access or folder does not exist!");
        } else {
            folderToBeUpdated.setName(folderCreateUpdateRequest.getName());
            folderRepository.save(folderToBeUpdated);
        }
    }

    @Override
    @Transactional
    public void deleteFolder(User user, Long folderId) {
        User savedUser = userService.getUserOrThrow(user);

        Folder folderToBeUpdated = folderRepository.findById(folderId).orElseThrow(
                () -> new FolderDoesNotExists("folder does not exists by id " + folderId)
        );

        boolean folderExistsForUser = folderRepository
                .existsByUserIdAndName(savedUser.getId(), folderToBeUpdated.getName());

        if (!folderExistsForUser) {
            // folder does not exist in combination of user and folder
            throw new FolderAccessDeniedException("User has no access or folder does not exist!");
        } else {
            cardRepository.deleteAllByFolderId(folderToBeUpdated.getId());
            folderRepository.deleteById(folderToBeUpdated.getId());
        }
    }
}

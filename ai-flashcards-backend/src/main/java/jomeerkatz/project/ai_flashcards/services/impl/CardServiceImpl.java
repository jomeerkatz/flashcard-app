package jomeerkatz.project.ai_flashcards.services.impl;

import jakarta.transaction.Transactional;
import jomeerkatz.project.ai_flashcards.domain.CardCreateUpdateRequest;
import jomeerkatz.project.ai_flashcards.domain.entities.Card;
import jomeerkatz.project.ai_flashcards.domain.entities.Folder;
import jomeerkatz.project.ai_flashcards.domain.entities.User;
import jomeerkatz.project.ai_flashcards.domain.enums.CardStatus;
import jomeerkatz.project.ai_flashcards.exceptions.CardException;
import jomeerkatz.project.ai_flashcards.exceptions.FolderAccessDeniedException;
import jomeerkatz.project.ai_flashcards.exceptions.FolderDoesNotExists;
import jomeerkatz.project.ai_flashcards.repositories.CardRepository;
import jomeerkatz.project.ai_flashcards.repositories.FolderRepository;
import jomeerkatz.project.ai_flashcards.services.CardService;
import jomeerkatz.project.ai_flashcards.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@AllArgsConstructor
@Service
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final UserService userService;
    private final FolderRepository folderRepository;

    @Override
    public Page<Card> getAllCards(User user, Long folderId, Pageable pageable) {
        // check if user even existing
        User savedUser = userService.getUserOrThrow(user);

        // get the actual folder, where data should get retrieved
        Folder savedFolder = folderRepository.findById(folderId).orElseThrow(
                () -> new FolderDoesNotExists("Folder does not exists!")
        );

        // check if the user has even access to the folder
        boolean userHasAccessToFolder = folderRepository.existsByUserIdAndName(savedUser.getId(), savedFolder.getName());

        if (!userHasAccessToFolder) {
            throw new FolderAccessDeniedException("User has not access to the folder!");
        } else {
            return cardRepository.findAllByUserIdAndFolderId(savedUser.getId(), folderId, pageable);
        }
    }

    @Override
    @Transactional
    public Card createCard(User user, Long folderId, CardCreateUpdateRequest cardCreateUpdateRequest) {
        // request comes to backend
        // we have to check, if the user is even existing bec without a user, we cant save it
        User savedUser = userService.getUserOrThrow(user);

        // if user exists, we have to check if the folder is existing too also
        Folder savedFolder = folderRepository.findById(folderId).orElseThrow(
                () -> new FolderDoesNotExists("Folder does not exists!")
        );
        // if folder is connected to user (user has access to the folder) - prevent random user create cards for folders
        // if user exists, if folder exists, if user has access to folder...
        boolean userHasAccessToFolder = folderRepository.existsByUserIdAndName(savedUser.getId(), savedFolder.getName());

        if (!userHasAccessToFolder) {
            throw new FolderAccessDeniedException("User has not access to the folder!");
        } else {
            Card card = Card.builder()
                    .user(savedUser)
                    .folder(savedFolder)
                    .question(cardCreateUpdateRequest.getQuestion())
                    .answer(cardCreateUpdateRequest.getAnswer())
                    .status(CardStatus.BAD)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            return cardRepository.save(card);
        }
    }

    @Override
    @Transactional
    public void updateCard(User user, Long folderId, CardCreateUpdateRequest card, Long cardId) {
        // request comes to backend
        // we have to check, if the user is even existing bec without a user, we cant save it
        User savedUser = userService.getUserOrThrow(user);

        // if user exists, we have to check if the folder is existing too also
        Folder savedFolder = folderRepository.findById(folderId).orElseThrow(
                () -> new FolderDoesNotExists("Folder does not exists!")
        );
        // if folder is connected to user (user has access to the folder) - prevent random user create cards for folders
        // if user exists, if folder exists, if user has access to folder...
        boolean userHasAccessToFolder = folderRepository.existsByUserIdAndName(savedUser.getId(), savedFolder.getName());

        if (!userHasAccessToFolder) {
            throw new FolderAccessDeniedException("User has not access to the folder!");
        } else {
            Optional<Card> savedCard = cardRepository.findByIdAndFolderId(cardId, savedFolder.getId());
            Card toBeUpdated = savedCard.orElseThrow(() -> new CardException("Card not existing or user has no access."));
            toBeUpdated.setUpdatedAt(LocalDateTime.now());
            toBeUpdated.setAnswer(card.getAnswer());
            toBeUpdated.setQuestion(card.getQuestion());
            cardRepository.save(toBeUpdated);
        }
    }

    @Override
    @Transactional
    public void deleteCard(User user, Long folderId, Long cardId) {
        // request comes to backend
        // we have to check, if the user is even existing bec without a user, we cant save it
        User savedUser = userService.getUserOrThrow(user);

        // if user exists, we have to check if the folder is existing too also
        Folder savedFolder = folderRepository.findById(folderId).orElseThrow(
                () -> new FolderDoesNotExists("Folder does not exists!")
        );
        // if folder is connected to user (user has access to the folder) - prevent random user create cards for folders
        // if user exists, if folder exists, if user has access to folder...
        boolean userHasAccessToFolder = folderRepository.existsByUserIdAndName(savedUser.getId(), savedFolder.getName());

        if (!userHasAccessToFolder) {
            throw new FolderAccessDeniedException("User has not access to the folder!");
        } else {
            Optional<Card> savedCard = cardRepository.findByIdAndFolderId(cardId, savedFolder.getId());
            Card toBeDeleted = savedCard.orElseThrow(() -> new CardException("Card not existing or user has no access."));
            cardRepository.deleteById(toBeDeleted.getId());
        }
    }

    @Override
    public Long getCountOfCardsByFoldeId(User user, Long folderId) {
        // request comes to backend
        // we have to check, if the user is even existing bec without a user, we cant save it
        User savedUser = userService.getUserOrThrow(user);

        // if user exists, we have to check if the folder is existing too also
        Folder savedFolder = folderRepository.findById(folderId).orElseThrow(
                () -> new FolderDoesNotExists("Folder does not exists!")
        );
        // if folder is connected to user (user has access to the folder) - prevent random user create cards for folders
        // if user exists, if folder exists, if user has access to folder...
        boolean userHasAccessToFolder = folderRepository.existsByUserIdAndName(savedUser.getId(), savedFolder.getName());

        if (!userHasAccessToFolder) {
            throw new FolderAccessDeniedException("User has not access to the folder!");
        } else {
            return cardRepository.countByFolderId(savedFolder.getId());
        }
    }
}


// todo: seems we have some boiler code here with getting user, getting folder, checking if folder and user is connected
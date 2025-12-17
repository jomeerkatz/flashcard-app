package jomeerkatz.project.ai_flashcards.services.impl;

import jomeerkatz.project.ai_flashcards.domain.entities.Card;
import jomeerkatz.project.ai_flashcards.domain.entities.Folder;
import jomeerkatz.project.ai_flashcards.domain.entities.User;
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
        Folder folder = folderRepository.findById(folderId).orElseThrow(
                () -> new FolderDoesNotExists("Folder does not exists!")
        );

        // check if the user has even access to the folder
        boolean userHasAccessToFolder = folderRepository.existsByUserIdAndName(savedUser.getId(), folder.getName());

        if (!userHasAccessToFolder) {
            throw new FolderAccessDeniedException("User has not access to the folder!");
        } else {
            return cardRepository.findAllByUserIdAndFolderId(savedUser.getId(), folderId, pageable);
        }
    }
}

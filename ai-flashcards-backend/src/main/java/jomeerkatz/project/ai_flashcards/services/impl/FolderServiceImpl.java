package jomeerkatz.project.ai_flashcards.services.impl;

import jomeerkatz.project.ai_flashcards.domain.FolderCreateUpdateRequest;
import jomeerkatz.project.ai_flashcards.domain.dtos.FolderDto;
import jomeerkatz.project.ai_flashcards.domain.entities.Folder;
import jomeerkatz.project.ai_flashcards.domain.entities.User;
import jomeerkatz.project.ai_flashcards.exceptions.FolderAlreadyExistsException;
import jomeerkatz.project.ai_flashcards.exceptions.UserNotFoundException;
import jomeerkatz.project.ai_flashcards.mappers.FolderMapper;
import jomeerkatz.project.ai_flashcards.repositories.FolderRepository;
import jomeerkatz.project.ai_flashcards.repositories.UserRepository;
import jomeerkatz.project.ai_flashcards.services.FolderService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@AllArgsConstructor
@Service
public class FolderServiceImpl implements FolderService {

    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    private final FolderMapper foldermapper;

    @Override
    public Folder createFolder(User user, FolderCreateUpdateRequest folderCreateUpdateRequest) {
        User savedUser = getUserOrThrow(user);

        boolean folderExistsForUser =  folderRepository
                .existsByUserIdAndName(savedUser.getId(), folderCreateUpdateRequest.getName());

        if(!folderExistsForUser) {
            return folderRepository.save(Folder.builder()
                            .name(folderCreateUpdateRequest.getName())
                            .user(savedUser)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                    .build());
        } else {
            throw new FolderAlreadyExistsException("folder with name " + folderCreateUpdateRequest.getName() + " already exists!")
        }
    }

    private User getUserOrThrow(User user) {
        return userRepository.findById(user.getId()).orElseThrow(
                () -> new UserNotFoundException("folder can't get created for user with id " + user.getId() + " because user doesn't exists!"));
    }
}

package jomeerkatz.project.ai_flashcards.services.impl;

import jomeerkatz.project.ai_flashcards.domain.FolderCreateUpdateRequest;
import jomeerkatz.project.ai_flashcards.domain.dtos.FolderDto;
import jomeerkatz.project.ai_flashcards.domain.entities.Folder;
import jomeerkatz.project.ai_flashcards.domain.entities.User;
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
    public FolderDto createFolder(User user, FolderCreateUpdateRequest folderCreateUpdateRequest) {
        // todo: check if user even exists or something else also handle if user is new. idk how
        // we can have the situation of user already exists or so
        // however if user doesnt exists throw custom exception
        // check if user exsists
        Folder newFolder = Folder.builder()
                .user(user)
                .name(folderCreateUpdateRequest.getName())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Folder savedFolder = folderRepository.save(newFolder);
        return foldermapper.toFolderDto(savedFolder);
    }

    private User getUserOrThrow(User user) {
        return userRepository.findById(user.getId()).orElseThrow(
                () -> new UserNotFoundException("folder can't get created for user with id " + user.getId()));
    }
}

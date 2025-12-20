package jomeerkatz.project.ai_flashcards;

import jomeerkatz.project.ai_flashcards.domain.FolderCreateUpdateRequest;
import jomeerkatz.project.ai_flashcards.domain.dtos.UserDto;
import jomeerkatz.project.ai_flashcards.domain.entities.Card;
import jomeerkatz.project.ai_flashcards.domain.entities.Folder;
import jomeerkatz.project.ai_flashcards.domain.entities.User;
import jomeerkatz.project.ai_flashcards.domain.enums.CardStatus;

import java.time.LocalDateTime;

public final class DataUtil {
    private DataUtil() {}

    public static UserDto getUserDtoExample1() {
        return UserDto.builder()
                .id(123123L)
                .build();
    }

    public static User getUserExample1() {
        return User.builder()
                .keycloakId("keycloak-id-1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static User getUserExample2() {
        return User.builder()
                .keycloakId("keycloak-id-2")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static User getUserExample1WithId() {
        return User.builder()
                .id(12312L)
                .keycloakId("keycloak-id-1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static User getUserExample2WithId() {
        return User.builder()
                .id(1123L)
                .keycloakId("keycloak-id-2")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Folder getFolderExample1(User user) {
        return Folder.builder()
                .user(user)
                .name("folder-name-1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Folder getFolderExample2(User user) {
        return Folder.builder()
                .user(user)
                .name("folder-name-2")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Folder getFolderExample1WithId(User user) {
        return Folder.builder()
                .id(1231122L)
                .user(user)
                .name("folder-name-2")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static FolderCreateUpdateRequest getFolderCreateUpdateRequest() {
        return FolderCreateUpdateRequest
                .builder()
                .name("example-name-Folder-12")
                .build();
    }

    public static Card getCardExample1(User user, Folder folder) {
        return Card.builder()
                .folder(folder)
                .user(user)
                .answer("answer 1")
                .question("question1")
                .status(CardStatus.BAD)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Card getCardExample2(User user, Folder folder) {
        return Card.builder()
                .folder(folder)
                .user(user)
                .answer("answer 2")
                .question("question2")
                .status(CardStatus.GOOD)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Card getCardExample3(User user, Folder folder) {
        return Card.builder()
                .folder(folder)
                .user(user)
                .answer("answer 3")
                .question("question3")
                .status(CardStatus.MEDIUM)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }


}

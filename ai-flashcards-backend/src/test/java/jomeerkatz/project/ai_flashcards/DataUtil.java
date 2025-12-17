package jomeerkatz.project.ai_flashcards;

import jomeerkatz.project.ai_flashcards.domain.entities.Card;
import jomeerkatz.project.ai_flashcards.domain.entities.Folder;
import jomeerkatz.project.ai_flashcards.domain.entities.User;
import jomeerkatz.project.ai_flashcards.domain.enums.CardStatus;

import java.time.LocalDateTime;

public class DataUtil {
    private DataUtil() {
    } // contructor is not visible -> thats how we prevent to make instances of this class

    public static String getKeycloakExampleId1() {
        return "keycloak-test-id-1";
    }

    public static String getKeycloakExampleId2() {
        return "keycloak-test-id-2";
    }

    public static String getKeycloakExampleId3() {
        return "keycloak-test-id-3";
    }

    public static String getFolderTestName1() {
        return "test-folder-name-1";
    }

    public static String getFolderTestName2() {
        return "test-folder-name-2";
    }

    public static String getFolderTestName3() {
        return "test-folder-name-3";
    }

    public static User getUserExample(String keycloakId) {
        return User.builder()
                .keycloakId(keycloakId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Folder getFolderExample(User user, String folderTestName) {
        return Folder.builder()
                .user(user)
                .name(folderTestName)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Card getCardExample1(User user, Folder folder) {
        return Card.builder()
                .user(user)
                .folder(folder)
                .question("this is a test question 1")
                .answer("this is a test answer 1")
                .status(CardStatus.BAD)
                .updatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Card getCardExample2(User user, Folder folder) {
        return Card.builder()
                .user(user)
                .folder(folder)
                .question("this is a test question 2")
                .answer("this is a test answer 2")
                .status(CardStatus.BAD)
                .updatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Card getCardExample3(User user, Folder folder) {
        return Card.builder()
                .user(user)
                .folder(folder)
                .question("this is a test question 3")
                .answer("this is a test answer 3")
                .status(CardStatus.BAD)
                .updatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
    }


}

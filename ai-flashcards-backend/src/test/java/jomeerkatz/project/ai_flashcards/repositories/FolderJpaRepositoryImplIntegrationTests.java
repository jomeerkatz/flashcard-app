package jomeerkatz.project.ai_flashcards.repositories;

import jomeerkatz.project.ai_flashcards.domain.entities.Folder;
import jomeerkatz.project.ai_flashcards.domain.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FolderJpaRepositoryImplIntegrationTests {
    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testThatReturnsFolderExistsTrue(){
        String keycloakTestId = "keycloak-test-id";
        LocalDateTime now = LocalDateTime.now();
        String folderTestName = "folder-test-name";

        User testUser = User.builder()
                .keycloakId(keycloakTestId)
                .createdAt(now)
                .updatedAt(now)
                .build();
        User savedUser = userRepository.save(testUser);

        Folder folder = Folder.builder()
                .user(savedUser)
                .name(folderTestName)
                .createdAt(now)
                .updatedAt(now)
                .build();

        folderRepository.save(folder);

        boolean folderExists = folderRepository.existsByUserIdAndName(savedUser.getId(), folder.getName());
        assertThat(folderExists).isTrue();

    }

    @Test
    public void testThatReturnsFolderExistsFalse(){
        String keycloakTestId = "keycloak-test-id";
        LocalDateTime now = LocalDateTime.now();
        String folderTestNameNotExisting = "not-existing-test-folder-name";

        User testUser = User.builder()
                .keycloakId(keycloakTestId)
                .createdAt(now)
                .updatedAt(now)
                .build();

        User savedUser = userRepository.save(testUser);

        boolean folderExists = folderRepository.existsByUserIdAndName(savedUser.getId(), folderTestNameNotExisting);
        assertThat(folderExists).isFalse();
    }

    @Test
    public void testThatReturnsFolderExistsFalseForDifferentUser(){
        LocalDateTime now = LocalDateTime.now();
        User user1 = userRepository.save(User.builder()
                .keycloakId("user1")
                .createdAt(now)
                .updatedAt(now)
                .build());

        User user2 = userRepository.save(User.builder()
                .keycloakId("user2")
                .createdAt(now)
                .updatedAt(now)
                .build());

        folderRepository.save(Folder.builder()
                .user(user1)
                .name("test-folder")
                .createdAt(now)
                .updatedAt(now)
                .build());

        // User2 shouldn't find user1's folder
        boolean exists = folderRepository.existsByUserIdAndName(user2.getId(), "test-folder");
        assertThat(exists).isFalse();
    }
}

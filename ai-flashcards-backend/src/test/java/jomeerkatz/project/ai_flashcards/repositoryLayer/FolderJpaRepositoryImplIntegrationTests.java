package jomeerkatz.project.ai_flashcards.repositoryLayer;

import jomeerkatz.project.ai_flashcards.DataUtil;
import jomeerkatz.project.ai_flashcards.domain.entities.Folder;
import jomeerkatz.project.ai_flashcards.domain.entities.User;
import jomeerkatz.project.ai_flashcards.repositories.FolderRepository;
import jomeerkatz.project.ai_flashcards.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FolderJpaRepositoryImplIntegrationTests {
    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private UserRepository userRepository;

    // tests for existsByUserIdAndName method
    @Test
    public void testThatReturnsFolderExistsTrue(){
        String keycloakTestId = DataUtil.getKeycloakExampleId1();
        String folderTestName = DataUtil.getFolderTestName1();

        User testUser = DataUtil.getUserExample(keycloakTestId);
        User savedUser = userRepository.save(testUser);

        Folder folder = DataUtil.getFolderExample(testUser, folderTestName);

        folderRepository.save(folder);

        boolean folderExists = folderRepository.existsByUserIdAndName(savedUser.getId(), folder.getName());
        assertThat(folderExists).isTrue();

    }

    @Test
    public void testThatReturnsFolderExistsFalse(){
        String keycloakTestId = DataUtil.getKeycloakExampleId1();
        String folderTestNameNotExisting = DataUtil.getFolderTestName1();

        User testUser = DataUtil.getUserExample(keycloakTestId);

        User savedUser = userRepository.save(testUser);

        boolean folderExists = folderRepository.existsByUserIdAndName(savedUser.getId(), folderTestNameNotExisting);
        assertThat(folderExists).isFalse();
    }

    @Test
    public void testThatReturnsFolderExistsFalseForDifferentUser(){
        // get keycloak id
        String keycloakTestId1 = DataUtil.getKeycloakExampleId1();
        // get user object example
        User testUser1 = DataUtil.getUserExample(keycloakTestId1);
        // save user example in db
        User user1 = userRepository.save(testUser1);
        // create test folder name
        String folderTestName = DataUtil.getFolderTestName1();
        // create a folder
        Folder exampleFolder = DataUtil.getFolderExample(user1, folderTestName);
        // save folder connected to the user 1
        Folder savedFolder = folderRepository.save(exampleFolder);

        String keycloakTestId2 = DataUtil.getKeycloakExampleId2();
        User testUser2 = DataUtil.getUserExample(keycloakTestId2);

        User user2 = userRepository.save(testUser2);

        // User2 shouldn't find user1's folder
        boolean exists = folderRepository.existsByUserIdAndName(user2.getId(), folderTestName);

        assertThat(exists).isFalse();
    }

    // tests for findAllByUserId method
    @Test
    public void testThatReturnsPagedFoldersForUser(){
        String keycloakTestId = DataUtil.getKeycloakExampleId1();
        User testUser = DataUtil.getUserExample(keycloakTestId);
        User savedUser = userRepository.save(testUser);

        Folder folder1 = DataUtil.getFolderExample(savedUser, DataUtil.getFolderTestName1());
        Folder folder2 = DataUtil.getFolderExample(savedUser, DataUtil.getFolderTestName2());
        Folder folder3 = DataUtil.getFolderExample(savedUser, DataUtil.getFolderTestName3());

        folderRepository.saveAll(java.util.List.of(folder1, folder2, folder3));

        Page<Folder> result = folderRepository.findAllByUserId(savedUser.getId(), PageRequest.of(0, 2));

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }

    @Test
    public void testThatReturnsEmptyPageWhenUserHasNoFolders(){
        String keycloakTestId = DataUtil.getKeycloakExampleId1();
        User testUser = DataUtil.getUserExample(keycloakTestId);
        User savedUser = userRepository.save(testUser);

        Page<Folder> result = folderRepository.findAllByUserId(savedUser.getId(), PageRequest.of(0, 10));

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    public void testThatReturnsFoldersOnlyForSpecificUser(){
        String keycloakTestId1 = DataUtil.getKeycloakExampleId1();
        User testUser1 = DataUtil.getUserExample(keycloakTestId1);
        User savedUser1 = userRepository.save(testUser1);

        String keycloakTestId2 = DataUtil.getKeycloakExampleId2();
        User testUser2 = DataUtil.getUserExample(keycloakTestId2);
        User savedUser2 = userRepository.save(testUser2);

        Folder folder1User1 = DataUtil.getFolderExample(savedUser1, DataUtil.getFolderTestName1());
        Folder folder2User1 = DataUtil.getFolderExample(savedUser1, DataUtil.getFolderTestName2());
        Folder folder1User2 = DataUtil.getFolderExample(savedUser2, DataUtil.getFolderTestName3());

        folderRepository.saveAll(java.util.List.of(folder1User1, folder2User1, folder1User2));

        Page<Folder> result = folderRepository.findAllByUserId(savedUser1.getId(), PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).allMatch(folder -> folder.getUser().getId().equals(savedUser1.getId()));
    }

    @Test
    public void testThatRespectsPaginationParameters(){
        String keycloakTestId = DataUtil.getKeycloakExampleId1();
        User testUser = DataUtil.getUserExample(keycloakTestId);
        User savedUser = userRepository.save(testUser);

        Folder folder1 = DataUtil.getFolderExample(savedUser, DataUtil.getFolderTestName1());
        Folder folder2 = DataUtil.getFolderExample(savedUser, DataUtil.getFolderTestName2());
        Folder folder3 = DataUtil.getFolderExample(savedUser, DataUtil.getFolderTestName3());

        folderRepository.saveAll(java.util.List.of(folder1, folder2, folder3));

        Page<Folder> firstPage = folderRepository.findAllByUserId(savedUser.getId(), PageRequest.of(0, 1));
        Page<Folder> secondPage = folderRepository.findAllByUserId(savedUser.getId(), PageRequest.of(1, 1));

        assertThat(firstPage.getContent()).hasSize(1);
        assertThat(secondPage.getContent()).hasSize(1);
        assertThat(firstPage.getContent().get(0)).isNotEqualTo(secondPage.getContent().get(0));
    }
}

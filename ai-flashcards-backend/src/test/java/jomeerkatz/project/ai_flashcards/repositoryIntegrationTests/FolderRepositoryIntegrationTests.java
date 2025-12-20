package jomeerkatz.project.ai_flashcards.repositoryIntegrationTests;

import jomeerkatz.project.ai_flashcards.DataUtil;
import jomeerkatz.project.ai_flashcards.domain.entities.Card;
import jomeerkatz.project.ai_flashcards.domain.entities.Folder;
import jomeerkatz.project.ai_flashcards.domain.entities.User;
import jomeerkatz.project.ai_flashcards.repositories.CardRepository;
import jomeerkatz.project.ai_flashcards.repositories.FolderRepository;
import jomeerkatz.project.ai_flashcards.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
@Testcontainers
public class FolderRepositoryIntegrationTests {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final FolderRepository folderRepository;

    @Autowired
    public FolderRepositoryIntegrationTests(final CardRepository cardRepository, final UserRepository userRepository, final FolderRepository folderRepository) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.folderRepository = folderRepository;
    }

    //    boolean existsByUserIdAndName(Long userId, String name);
    @Test
    public void TestThatFolderBelongsToUserReturnsTrue() {
        User savedUser = userRepository.save(DataUtil.getUserExample1());
        Folder savedFolder = folderRepository.save(DataUtil.getFolderExample1(savedUser));

        boolean result = folderRepository.existsByUserIdAndName(savedUser.getId(), savedFolder.getName());

        assertThat(result).isTrue();
    }

    @Test
    public void TestThatFolderBelongsNotToUserReturnsFalse() {
        User savedUser = userRepository.save(DataUtil.getUserExample1());

        boolean result = folderRepository.existsByUserIdAndName(savedUser.getId(), "any-folder-test-name");

        assertThat(result).isFalse();
    }

    //    Page<Folder> findAllByUserId(Long userId, Pageable pageable);
    @Test
    public void TestThatFindAllFolderByUserIdReturnsAllFolder() {
        User savedUser1 = userRepository.save(DataUtil.getUserExample1());
        User savedUser2 = userRepository.save(DataUtil.getUserExample2());
        Folder savedFolder1 = folderRepository.save(DataUtil.getFolderExample1(savedUser1));
        Folder savedFolder2 = folderRepository.save(DataUtil.getFolderExample2(savedUser2));

        Pageable pageable = PageRequest.of(0, 5);

        Page<Folder> resultFolder = folderRepository.findAllByUserId(savedUser1.getId(), pageable);

        assertThat(resultFolder).isNotEmpty();
        assertThat(resultFolder.getContent()).hasSize(1);
        assertThat(resultFolder.getContent()).containsExactly(savedFolder1);
    }

    @Test
    public void TestThatFindNotFolderByUserIdReturnsEmpty() {
        User savedUser1 = userRepository.save(DataUtil.getUserExample1());
        User savedUser2 = userRepository.save(DataUtil.getUserExample2());
        Folder savedFolder1 = folderRepository.save(DataUtil.getFolderExample1(savedUser2));
        Folder savedFolder2 = folderRepository.save(DataUtil.getFolderExample2(savedUser2));

        Pageable pageable = PageRequest.of(0, 5);

        Page<Folder> resultFolder = folderRepository.findAllByUserId(savedUser1.getId(), pageable);

        assertThat(resultFolder).isEmpty();
    }
}

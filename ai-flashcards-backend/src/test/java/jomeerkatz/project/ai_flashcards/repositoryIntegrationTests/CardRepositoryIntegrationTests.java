package jomeerkatz.project.ai_flashcards.repositoryIntegrationTests;

import jakarta.transaction.Transactional;
import jomeerkatz.project.ai_flashcards.DataUtil;
import jomeerkatz.project.ai_flashcards.domain.entities.Card;
import jomeerkatz.project.ai_flashcards.domain.entities.Folder;
import jomeerkatz.project.ai_flashcards.domain.entities.User;
import jomeerkatz.project.ai_flashcards.domain.enums.CardStatus;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
@Testcontainers
public class CardRepositoryIntegrationTests {

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
    public CardRepositoryIntegrationTests(final CardRepository cardRepository, final UserRepository userRepository, final FolderRepository folderRepository) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.folderRepository = folderRepository;
    }

//    Page<Card> findAllByUserIdAndFolderId(Long userId, Long folderId, Pageable pageable);
    @Test
    public void TestThatFindAllCardsByUserIdAndFolderIdSuccessfully() {
        User savedUser = userRepository.save(DataUtil.getUserExample1());
        Folder savedFolder = folderRepository.save(DataUtil.getFolderExample1(savedUser));
        Card savedCard1 = cardRepository.save(DataUtil.getCardExample1(savedUser, savedFolder));
        Card savedCard2 = cardRepository.save(DataUtil.getCardExample2(savedUser, savedFolder));
        Card savedCard3 = cardRepository.save(DataUtil.getCardExample3(savedUser, savedFolder));

        Pageable pageable = PageRequest.of(0, 5);

        Page<Card> listOfCards = cardRepository.findAllByUserIdAndFolderId(
                savedUser.getId(), savedFolder.getId(), pageable
        );

        assertThat(listOfCards).hasSize(3);
        assertThat(listOfCards).containsExactly(savedCard1, savedCard2, savedCard3);
    }

    @Test
    public void TestThatFindAllCardsByUserIdAndFolderIdSuccessfullyAndReturnsOnlyCardsInsideFolder() {
        User savedUser1 = userRepository.save(DataUtil.getUserExample1());
        User savedUser2 = userRepository.save(DataUtil.getUserExample2());

        Folder savedFolder1 = folderRepository.save(DataUtil.getFolderExample1(savedUser1));
        Folder savedFolder2 = folderRepository.save(DataUtil.getFolderExample2(savedUser2));

        Card savedCard1 = cardRepository.save(DataUtil.getCardExample1(savedUser1, savedFolder1));
        Card savedCard2 = cardRepository.save(DataUtil.getCardExample2(savedUser1, savedFolder1));
        Card savedCard3 = cardRepository.save(DataUtil.getCardExample3(savedUser2, savedFolder2));

        Pageable pageable1 = PageRequest.of(0, 5);

        Page<Card> listOfCards1 = cardRepository.findAllByUserIdAndFolderId(
                savedUser1.getId(), savedFolder1.getId(), pageable1
        );

        Page<Card> listOfCards2 = cardRepository.findAllByUserIdAndFolderId(
                savedUser2.getId(), savedFolder2.getId(), pageable1
        );

        assertThat(listOfCards1).hasSize(2);
        assertThat(listOfCards1).containsExactly(savedCard1, savedCard2);

        assertThat(listOfCards2).hasSize(1);
        assertThat(listOfCards2).contains(savedCard3);

    }


//    Optional<Card> findByIdAndFolderId(Long id, Long folderId);
    @Test
    public void TestThatFindsSuccessfullyCardByIdAndFolderId() {
        User savedUser = userRepository.save(DataUtil.getUserExample1());
        Folder savedFolder = folderRepository.save(DataUtil.getFolderExample1(savedUser));
        Card savedCard1 = cardRepository.save(DataUtil.getCardExample1(savedUser, savedFolder));

        Optional<Card> resultCard = cardRepository.findByIdAndFolderId(savedCard1.getId(), savedFolder.getId());

        assertThat(resultCard).isNotEmpty();
        assertThat(resultCard.get()).isEqualTo(savedCard1);
    }

    @Test
    public void TestThatFindsNotSuccessfullyCardByIdAndFolderId() {
        User savedUser = userRepository.save(DataUtil.getUserExample1());
        Folder savedFolder = folderRepository.save(DataUtil.getFolderExample1(savedUser));
        Long idExampleNotExisting = 1L;
        Optional<Card> resultCard = cardRepository.findByIdAndFolderId(idExampleNotExisting, savedFolder.getId());

        assertThat(resultCard).isEmpty();
    }

//    void deleteAllByFolderId(Long folderId);
    @Test
    @Transactional
    public void TestThatDeletesSuccessfullyAllCardsInsideFolderAndNotBeFoundAgain() {
        User savedUser = userRepository.save(DataUtil.getUserExample1());
        Folder savedFolder = folderRepository.save(DataUtil.getFolderExample1(savedUser));
        Card savedCard1 = cardRepository.save(DataUtil.getCardExample1(savedUser, savedFolder));
        Card savedCard2 = cardRepository.save(DataUtil.getCardExample2(savedUser, savedFolder));
        Card savedCard3 = cardRepository.save(DataUtil.getCardExample3(savedUser, savedFolder));

        cardRepository.deleteAllByFolderId(savedFolder.getId());

        Optional<Card> resultCard1 = cardRepository.findById(savedCard1.getId());
        Optional<Card> resultCard2 = cardRepository.findById(savedCard2.getId());
        Optional<Card> resultCard3 = cardRepository.findById(savedCard3.getId());

        assertThat(resultCard1).isEmpty();
        assertThat(resultCard2).isEmpty();
        assertThat(resultCard3).isEmpty();
    }

//    Long countByFolderId(Long folderId);
    @Test
    public void TestThatCountCorrectlyExistingCardsInFolder() {
        User savedUser = userRepository.save(DataUtil.getUserExample1());
        Folder savedFolder = folderRepository.save(DataUtil.getFolderExample1(savedUser));
        Card savedCard1 = cardRepository.save(DataUtil.getCardExample1(savedUser, savedFolder));
        Card savedCard2 = cardRepository.save(DataUtil.getCardExample2(savedUser, savedFolder));
        Card savedCard3 = cardRepository.save(DataUtil.getCardExample3(savedUser, savedFolder));

        Long resultCount = cardRepository.countByFolderId(savedFolder.getId());

        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void TestThatCountCorrectlyNotExistingCardsInFolderReturnsZero() {
        User savedUser = userRepository.save(DataUtil.getUserExample1());
        Folder savedFolder = folderRepository.save(DataUtil.getFolderExample1(savedUser));

        Long resultCount = cardRepository.countByFolderId(savedFolder.getId());

        assertThat(resultCount).isEqualTo(0);
    }

//    Page<Card> findAllByUserIdAndFolderIdAndStatus(Long userId, Long folderId, CardStatus status, Pageable pageable);
    @Test
    public void TestThatFindSuccessfullyCardWhichHasUserIdFolderIdAndStatusReturnsCorrectCard() {
        User savedUser = userRepository.save(DataUtil.getUserExample1());
        Folder savedFolder = folderRepository.save(DataUtil.getFolderExample1(savedUser));
        Card savedCard1 = cardRepository.save(DataUtil.getCardExample1(savedUser, savedFolder)); // bad
        Card savedCard2 = cardRepository.save(DataUtil.getCardExample2(savedUser, savedFolder)); // medium
        Card savedCard3 = cardRepository.save(DataUtil.getCardExample3(savedUser, savedFolder)); // good

        CardStatus cardStatus = CardStatus.BAD;

        Pageable pageable = PageRequest.of(0, 5);


        Page<Card> resultPage = cardRepository.findAllByUserIdAndFolderIdAndStatus(savedUser.getId(),
                savedFolder.getId(),
                cardStatus,
                pageable);

        assertThat(resultPage).hasSize(1);
        assertThat(resultPage.getContent().getFirst()).isEqualTo(savedCard1);
    }

    @Test
    public void TestThatDoNotFindCardWhichHasUserIdFolderIdAndStatusReturnsEmpty() {
        User savedUser = userRepository.save(DataUtil.getUserExample1());
        Folder savedFolder = folderRepository.save(DataUtil.getFolderExample1(savedUser));
        Card savedCard2 = cardRepository.save(DataUtil.getCardExample2(savedUser, savedFolder)); // medium
        Card savedCard3 = cardRepository.save(DataUtil.getCardExample3(savedUser, savedFolder)); // good

        CardStatus cardStatus = CardStatus.BAD;

        Pageable pageable = PageRequest.of(0, 5);


        Page<Card> resultPage = cardRepository.findAllByUserIdAndFolderIdAndStatus(savedUser.getId(),
                savedFolder.getId(),
                cardStatus,
                pageable);

        assertThat(resultPage).hasSize(0);
        assertThat(resultPage).isEmpty();
    }

}

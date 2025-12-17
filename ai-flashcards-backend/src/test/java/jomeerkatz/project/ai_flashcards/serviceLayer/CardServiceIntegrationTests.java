package jomeerkatz.project.ai_flashcards.serviceLayer;


import jomeerkatz.project.ai_flashcards.DataUtil;
import jomeerkatz.project.ai_flashcards.domain.entities.Card;
import jomeerkatz.project.ai_flashcards.domain.entities.Folder;
import jomeerkatz.project.ai_flashcards.domain.entities.User;
import jomeerkatz.project.ai_flashcards.repositories.CardRepository;
import jomeerkatz.project.ai_flashcards.repositories.FolderRepository;
import jomeerkatz.project.ai_flashcards.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;


import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class CardServiceIntegrationTests {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testThatGetAllCardsSuccessfullyByUserAndFolder(){
        String keycloakIdExample = DataUtil.getKeycloakExampleId1();

        User user = DataUtil.getUserExample(keycloakIdExample);

        String folderNameExample = DataUtil.getFolderTestName1();

        Folder folder = DataUtil.getFolderExample(user, folderNameExample);

        Card card1 = DataUtil.getCardExample1(user, folder);
        Card card2 = DataUtil.getCardExample2(user, folder);
        Card card3 = DataUtil.getCardExample3(user, folder);

        User savedUser = userRepository.save(user);

        Folder savedFolder = folderRepository.save(folder);

        Card savedCard1 = cardRepository.save(card1);
        Card savedCard2 = cardRepository.save(card2);
        Card savedCard3 = cardRepository.save(card3);

        Pageable pageable = PageRequest.of(0, 5);

        Page<Card> allByUserIdAndFolderId = cardRepository.findAllByUserIdAndFolderId(savedUser.getId(), savedFolder.getId(), pageable);

        assertThat(allByUserIdAndFolderId.getContent()).hasSize(3);
        assertThat(allByUserIdAndFolderId.getContent()).containsExactly(savedCard1, savedCard2, savedCard3);

    }

}

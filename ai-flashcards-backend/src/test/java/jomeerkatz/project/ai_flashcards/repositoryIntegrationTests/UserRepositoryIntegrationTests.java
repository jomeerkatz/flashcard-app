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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
@Testcontainers
public class UserRepositoryIntegrationTests {
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
    public UserRepositoryIntegrationTests(final CardRepository cardRepository, final UserRepository userRepository, final FolderRepository folderRepository) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.folderRepository = folderRepository;
    }

    // Optional<User> findByKeycloakId(String keycloakId);
    @Test
    public void TestThatFindSuccessfullyUserByKeycloakIdReturnsUser(){
        User savedUser = userRepository.save(DataUtil.getUserExample1());

        Optional<User> resultUser = userRepository.findByKeycloakId(savedUser.getKeycloakId());

        assertThat(resultUser).isNotEmpty();
        assertThat(resultUser).contains(savedUser);
    }

    @Test
    public void TestThatFindNotUserByKeycloakIdReturnsEmpty(){

        Optional<User> resultUser = userRepository.findByKeycloakId("random keycloak id which is not existing");

        assertThat(resultUser).isEmpty();
    }
}

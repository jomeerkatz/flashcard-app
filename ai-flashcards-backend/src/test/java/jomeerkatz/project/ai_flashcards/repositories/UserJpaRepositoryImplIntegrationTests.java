package jomeerkatz.project.ai_flashcards.repositories;

import jomeerkatz.project.ai_flashcards.domain.UserCreateUpdateRequest;
import jomeerkatz.project.ai_flashcards.domain.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserJpaRepositoryImplIntegrationTests {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testThatFindsExistingUser() {
        String keycloakTestId = "keycloak-test-id";
        LocalDateTime now = LocalDateTime.now();

        User testUser = User.builder()
                .keycloakId(keycloakTestId)
                .createdAt(now)
                .updatedAt(now)
                .build();
        User savedUser = userRepository.save(testUser);

        Optional<User> result = userRepository.findByKeycloakId(keycloakTestId);

        assertThat(result).isPresent();

        assertThat(result).contains(savedUser);
    }

    @Test
    public void testThatDoesNotFindUser() {
        String keycloakTestId = "keycloak-test-id";

        Optional<User> result = userRepository.findByKeycloakId(keycloakTestId);

        assertThat(result).isEmpty();
    }

}

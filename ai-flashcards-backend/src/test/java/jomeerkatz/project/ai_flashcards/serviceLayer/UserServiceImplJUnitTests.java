package jomeerkatz.project.ai_flashcards.serviceLayer;

import jomeerkatz.project.ai_flashcards.DataUtil;
import jomeerkatz.project.ai_flashcards.domain.entities.User;
import jomeerkatz.project.ai_flashcards.repositories.UserRepository;
import jomeerkatz.project.ai_flashcards.services.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplJUnitTests {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Test
    public void testThatThrowsDataIntegrationViolExceptionAndReturnsExistingOne() {
        String keycloakId = DataUtil.getKeycloakExampleId1();
        User savedUser = DataUtil.getUserExample(keycloakId);

        when(userRepository.findByKeycloakId(keycloakId))
                .thenReturn(Optional.empty()) // first steps
                .thenReturn(Optional.of(savedUser)); // third step

        when(userRepository.save(any()))
                .thenThrow(new DataIntegrityViolationException("violation text")); // second step

        User result = userService.createOrFindUser(savedUser);

        assertThat(result).isEqualTo(savedUser);
    }

    @Test
    public void testThatThrowsIllegalStateExcAndReturnsExactMessageAsImplFile(){
        String keycloakId = DataUtil.getKeycloakExampleId1();
        User savedUser = DataUtil.getUserExample(keycloakId);

        when(userRepository.findByKeycloakId(keycloakId))
                .thenReturn(Optional.empty()) // first time
                .thenReturn((Optional.empty())); // second time -> now exception

        when(userRepository.save(any()))
                .thenThrow(new DataIntegrityViolationException("violation text"));

        assertThatThrownBy(() -> userService.createOrFindUser(savedUser))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("User exists but could not be loaded after constraint violation");
    }
}

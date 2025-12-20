package jomeerkatz.project.ai_flashcards.serviceJUnitTests;

import jomeerkatz.project.ai_flashcards.DataUtil;
import jomeerkatz.project.ai_flashcards.domain.entities.User;
import jomeerkatz.project.ai_flashcards.exceptions.UserNotFoundException;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplJUnitTests {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

//    User createOrFindUser(User user);

    @Test
    public void TestThatReturnsExistingUserReturnsUser() {
        User newUser = DataUtil.getUserExample1WithId();
        when(userRepository.findByKeycloakId(newUser.getKeycloakId()))
                .thenReturn(Optional.of(newUser));

        User resultUser = userService.createOrFindUser(newUser);

        assertThat(resultUser).isEqualTo(newUser);
        verify(userRepository, never()).save(any());
    }

    @Test
    public void TestThatReturnsNewUserReturnsUser() {
        User newUser = DataUtil.getUserExample1WithId();
        when(userRepository.findByKeycloakId(newUser.getKeycloakId()))
                .thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(newUser);

        User resultUser = userService.createOrFindUser(newUser);

        assertThat(resultUser).isEqualTo(newUser);
        verify(userRepository).save(any());
    }

    @Test
    public void TestCreateOrFindUserHandlesDataIntegrityViolation() {
        User newUser = DataUtil.getUserExample1WithId();

        when(userRepository.findByKeycloakId(newUser.getKeycloakId()))
                .thenReturn(Optional.empty())
                        .thenReturn(Optional.of(newUser));

        when(userRepository.save(any()))
                .thenThrow(new DataIntegrityViolationException("Contraint Violation"));

        User resultUser = userService.createOrFindUser(newUser);

        assertThat(resultUser).isEqualTo(newUser);
    }

    @Test
    public void TestCreateOrFindUserThrowsIllegalStateExceptionWhenRecoveryFails() {
        User newUser = DataUtil.getUserExample1WithId();
        when(userRepository.findByKeycloakId(newUser.getKeycloakId()))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.empty());
        when(userRepository.save(any()))
                .thenThrow(new DataIntegrityViolationException("Constraint violation"));

        assertThatThrownBy(() -> userService.createOrFindUser(newUser))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("User exists but could not be loaded after constraint violation");
    }

//    User getUserOrThrow(User user);

    @Test
    public void TestThatGetUser () {
        User newUser = DataUtil.getUserExample1WithId();

        when(userRepository.findByKeycloakId(newUser.getKeycloakId()))
                .thenReturn(Optional.of(newUser));

        User resultUser = userService.getUserOrThrow(newUser);

        assertThat(resultUser).isEqualTo(newUser);
    }

    @Test
    public void TestThatTriesGetUserButThrowsAndReturnsUserNotFoundException() {
        User newUser = DataUtil.getUserExample1WithId();

        when(userRepository.findByKeycloakId(newUser.getKeycloakId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserOrThrow(newUser))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with keycloak id!");
    }
}

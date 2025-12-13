package jomeerkatz.project.ai_flashcards.services.impl;

import jakarta.transaction.Transactional;
import jomeerkatz.project.ai_flashcards.domain.UserCreateUpdateRequest;
import jomeerkatz.project.ai_flashcards.domain.entities.User;
import jomeerkatz.project.ai_flashcards.exceptions.UserNotFoundException;
import jomeerkatz.project.ai_flashcards.repositories.UserRepository;
import jomeerkatz.project.ai_flashcards.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public User createOrFindUser(UserCreateUpdateRequest userCreateUpdateRequest) {
        String keycloakId = userCreateUpdateRequest.getKeycloakId();
        try {
            return userRepository.findByKeycloakId(keycloakId).orElseGet(() ->
                    userRepository.save(
                            User.builder()
                                    .keycloakId(keycloakId)
                                    .createdAt(LocalDateTime.now())
                                    .updatedAt(LocalDateTime.now())
                                    .build()
                    ));
        } catch (DataIntegrityViolationException ex) {
            return userRepository.findByKeycloakId(keycloakId).orElseThrow(() ->
                    new IllegalStateException(
                            "User exists but could not be loaded after constraint violation"
                    )
            );
        }
    }

}

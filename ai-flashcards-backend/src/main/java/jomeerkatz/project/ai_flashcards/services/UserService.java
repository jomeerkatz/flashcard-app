package jomeerkatz.project.ai_flashcards.services;


import jomeerkatz.project.ai_flashcards.domain.UserCreateUpdateRequest;
import jomeerkatz.project.ai_flashcards.domain.entities.User;

public interface UserService {
    User createOrFindUser(User user);
    User getUserOrThrow(User user);
}

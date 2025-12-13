package jomeerkatz.project.ai_flashcards.services;


import jomeerkatz.project.ai_flashcards.domain.UserCreateUpdateRequest;
import jomeerkatz.project.ai_flashcards.domain.entities.User;

public interface UserService {
    // todo: implement to create a user -> really important for creating a folder or cards and so on. if no user
    //  exists, we need to throw an exception. when in frontend a new user sign up we send a userdto to backend and
    //  create one which is super important
    User createOrFindUser(UserCreateUpdateRequest userCreateUpdateRequest);

}

package jomeerkatz.project.ai_flashcards.controllerIntegrationTests;

import jomeerkatz.project.ai_flashcards.controllers.ErrorController;
import jomeerkatz.project.ai_flashcards.controllers.UserController;
import jomeerkatz.project.ai_flashcards.domain.entities.User;
import jomeerkatz.project.ai_flashcards.exceptions.FolderAccessDeniedException;
import jomeerkatz.project.ai_flashcards.exceptions.FolderAlreadyExistsException;
import jomeerkatz.project.ai_flashcards.exceptions.UserNotFoundException;
import jomeerkatz.project.ai_flashcards.mappers.UserMapper;
import jomeerkatz.project.ai_flashcards.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {UserController.class, ErrorController.class})
public class ErrorControllerTests {
    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserMapper userMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testUserNotFoundExceptionReturns404() throws Exception {
        when(userService.createOrFindUser(any(User.class)))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(post("/api/users")
                        .with(jwt().jwt(jwt -> jwt.subject("test-id"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    public void testFolderAlreadyExistsReturns400() throws Exception {
        when(userService.createOrFindUser(any(User.class)))
                .thenThrow(new FolderAlreadyExistsException("Folder exists"));

        mockMvc.perform(post("/api/users")
                        .with(jwt().jwt(jwt -> jwt.subject("test-id"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    public void testFolderAccessDeniedReturns403() throws Exception {
        when(userService.createOrFindUser(any(User.class)))
                .thenThrow(new FolderAccessDeniedException("Access denied"));

        mockMvc.perform(post("/api/users")
                        .with(jwt().jwt(jwt -> jwt.subject("test-id"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }
}

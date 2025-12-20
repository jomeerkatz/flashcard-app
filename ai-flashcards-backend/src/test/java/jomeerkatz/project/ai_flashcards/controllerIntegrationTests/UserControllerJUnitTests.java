package jomeerkatz.project.ai_flashcards.controllerIntegrationTests;

import jomeerkatz.project.ai_flashcards.DataUtil;
import jomeerkatz.project.ai_flashcards.controllers.UserController;
import jomeerkatz.project.ai_flashcards.domain.dtos.UserDto;
import jomeerkatz.project.ai_flashcards.domain.entities.User;
import jomeerkatz.project.ai_flashcards.mappers.UserMapper;
import jomeerkatz.project.ai_flashcards.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
public class UserControllerJUnitTests {
    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserMapper userMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCreateUserReturnsUserDtoSuccessfully() throws Exception {
        User user = DataUtil.getUserExample2WithId();
        UserDto userDto = DataUtil.getUserDtoExample1();

        when(userService.createOrFindUser(any(User.class)))
                .thenReturn(user);
        when(userMapper.toUserDto(user))
                .thenReturn(userDto);

        mockMvc.perform(post("/api/users")
                        .with(jwt().jwt(jwt -> jwt.subject(user.getKeycloakId()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()));

        verify(userService).createOrFindUser(any(User.class));
        verify(userMapper).toUserDto(user);
    }

    @Test
    public void testCreateUserWithoutJwtReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/users")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}
package jomeerkatz.project.ai_flashcards.controllers;

import jomeerkatz.project.ai_flashcards.domain.dtos.UserDto;
import jomeerkatz.project.ai_flashcards.domain.entities.User;
import jomeerkatz.project.ai_flashcards.mappers.UserMapper;
import jomeerkatz.project.ai_flashcards.services.UserService;
import jomeerkatz.project.ai_flashcards.utility.JwtMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping(path = "/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@AuthenticationPrincipal Jwt jwt) {
        User newUser = JwtMapper.toUser(jwt);
        return ResponseEntity.ok(userMapper.toUserDto(userService.createOrFindUser(newUser)));
    }
}


// todo: refactor ideas: using just the user endpoint for creating, delete, update folders and also cards